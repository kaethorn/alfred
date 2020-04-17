// MongoDB testbench, used to develop queries that are later moved to Spring Data MongoDB.

const mongoose = require('mongoose');
mongoose.connect('mongodb://localhost/alfred');
require('node-json-color-stringify');

// eslint-disable-next-line no-console
const log = console.log;
// eslint-disable-next-line no-console
const logError = console.error;

const comicSchema = mongoose.Schema({
  characters : String,
  colorist   : String,
  coverArtist: String,
  editor     : String,
  inker      : String,
  letterer   : String,
  manga      : Boolean,
  month      : Number,
  notes      : String,
  number     : String,
  pageCount  : Number,
  path       : String,
  penciller  : String,
  position   : String,
  publisher  : String,
  series     : String,
  summary    : String,
  teams      : String,
  thumbnail  : String,
  title      : String,
  volume     : String,
  web        : String,
  writer     : String,
  year       : Number
});

const progressSchema = mongoose.Schema({
  comicId    : String,
  currentPage: Number,
  lastRead   : Date,
  read       : Boolean,
  userId     : String
});

const Comic = mongoose.model('Comic', comicSchema, 'comic');
const Progress = mongoose.model('Progress', progressSchema, 'progress');
const userId = '104414564769351832134';
// const userId2 = '107859401383492803405';
const mockUser = 'oauth2-mock-user-id';

const progress = () => Progress.find();
const findAllVolumes = () =>
  Comic.aggregate()
    .lookup({
      as: 'progress',
      foreignField: 'comicId',
      from: 'progress',
      localField: '_id'
    })
    .match({ publisher: 'DC Comics', series: 'Batgirl' })
    .sort({ position: 1 })
    .group({
      _id         : { volume: '$volume' },
      firstComicId: { $first: '$_id' },
      issueCount  : { $sum: 1 },
      publisher   : { $last: '$publisher' },
      read        : { $min: '$read' },
      readCount   : { $sum: { $cond: [ '$read', 1, 0 ] } },
      series      : { $last: '$series' },
      volume      : { $last: '$volume' }
    })
    .sort({ volume: 1 });

const findAllLastReadPerVolume = () =>
  Comic.aggregate()
    .sort({ position: 1 })
    .lookup({
      as: 'progress',
      foreignField: 'comicId',
      from: 'progress',
      localField: '_id'
    })
    .replaceRoot({
      $mergeObjects: [{
        $arrayElemAt: [{
          $filter: {
            as   : 'item',
            cond : { $eq: [ '$$item.userId', userId ] },
            input: '$progress'
          }
        }, 0 ]
      }, '$$ROOT' ]
    })
    .project({ comicId: 0, progress: 0, userId: 0 })
    .group({
      _id       : { publisher: '$publisher', series: '$series', volume: '$volume' },
      comics: { $push: '$$ROOT' },
      readCount : { $sum: { $cond:
        [{ $or: [ '$currentPage', '$read' ] }, 1, 0 ]
      } },
      volumeRead: { $min: { $cond: [ '$read', true, false ] } }
    })
    .match({ $expr: { $eq: [ '$volumeRead', false ] } })
    .match({ $expr: { $gt: [ '$readCount', 0 ] } })
    .project({
      comics: {
        $filter: {
          as   : 'item',
          cond : { $ne: [ '$$item.read', true ] },
          input: '$comics'
        }
      }
    })
    // At this point, all we have volumes of comics that are unread.
    .replaceRoot({ $arrayElemAt: [ '$comics', 0 ] })
    .sort({ lastRead: -1 });

// Publishers -> Series -> Volumes
const publishers = () =>
  Comic.aggregate()
    .lookup({
      as: 'progress',
      foreignField: 'comicId',
      from: 'progress',
      localField: '_id'
    })
    .replaceRoot({
      $mergeObjects: [
        '$$ROOT',
        { $arrayElemAt: [{
          $filter: {
            as   : 'item',
            cond : { $eq: [ '$$item.userId', userId ] },
            input: '$progress'
          }
        }, 0 ] },
        { _id: '$$ROOT._id' }
      ]
    })
    .project({ comicId: 0, progress: 0, userId: 0 })

    .sort({ position: 1 })
    .group({
      _id       : { publisher: '$publisher', series: '$series', volume: '$volume'  },
      issueCount: { $sum: 1 },
      read      : { $min: '$read' },
      readCount : { $sum: { $cond: [ '$read', 1, 0 ] } },
      volume    : { $last: '$volume' }
    })
    .sort({ volume: 1 })
    .group({
      _id    : { publisher: '$_id.publisher', series: '$_id.series'  },
      series : { $last: '$_id.series' },
      volumes: { $push: {
        issueCount: '$issueCount',
        position  : '$position',
        read      : '$read',
        readCount : '$readCount',
        volume    : '$volume'
      } }
    })
    .sort({ series: -1 })
    .group({
      _id      : { publisher: '$_id.publisher' },
      publisher: { $last: '$_id.publisher' },
      series   : { $addToSet: { series: '$_id.series', volumes: '$volumes' } }
    })
    .sort({ publisher: 1 });

const findLastReadForVolume = () =>
  Comic.aggregate()
    // should return #1 (all read)
    // .match({ publisher: 'DC Comics', series: 'Batgirl', volume: '2000' })
    // should return #2 (first read)
    .match({ publisher: 'DC Comics', series: 'Batgirl', volume: '2016' })

    .lookup({
      as: 'progress',
      foreignField: 'comicId',
      from: 'progress',
      localField: '_id'
    })
    .replaceRoot({
      $mergeObjects: [
        '$$ROOT',
        { $arrayElemAt: [{
          $filter: {
            as   : 'item',
            cond : { $eq: [ '$$item.userId', mockUser ] },
            input: '$progress'
          }
        }, 0 ] },
        { _id: '$$ROOT._id' }
      ]
    })
    .project({ comicId: 0, progress: 0, userId: 0 })

    .sort({ position: 1 })
    .sort({ read: 1 })
    .limit(1);

const findAllByPublisherAndSeriesAndVolumeOrderByPosition = () =>
  Comic.aggregate()
    .match({ publisher: 'DC Comics', series: 'Batgirl', volume: '2000' })
    .lookup({
      as: 'progress',
      foreignField: 'comicId',
      from: 'progress',
      localField: '_id'
    })
    .replaceRoot({
      $mergeObjects: [
        { $arrayElemAt: [{
          $filter: {
            as   : 'item',
            cond : { $eq: [ '$$item.userId', userId ] },
            input: '$progress'
          }
        }, 0 ] },
        '$$ROOT'
      ]
    })
    .project({ comicId: 0, progress: 0, userId: 0 })
    .sort({ position: 1 });

const markAllAsReadUntil = () =>
  Comic.aggregate()
    .match({ publisher: 'DC Comics', series: 'Batgirl', volume: '2000' })
    .match({ position: { $lte: '0002.0' } });

const db = mongoose.connection;
db.on('error', error => {
  logError(`connection error: ${ error }`);
});
db.once('open', () => {

  let method;

  switch (process.argv[2]) {
    default:
    case 'findAllLastReadPerVolume':
      method = findAllLastReadPerVolume;
      break;
    case 'progress':
      method = progress;
      break;
    case 'publishers':
    case 'p':
      method = publishers;
      break;
    case 'findAllByPublisherAndSeriesAndVolumeOrderByPosition':
      method = findAllByPublisherAndSeriesAndVolumeOrderByPosition;
      break;
    case 'findLastReadForVolume':
      method = findLastReadForVolume;
      break;
    case 'markAllAsReadUntil':
      method = markAllAsReadUntil;
      break;
    case 'findAllVolumes':
      method = findAllVolumes;
      break;
  }

  method()
    .exec((err, res) => {
      if (err) {
        return logError(`exec error:${ err }`);
      }
      // log(res);
      log(JSON.colorStringify(res, null, 2));
      log(`Result length: ${ res.length }`);
      mongoose.connection.close();
    });
});
