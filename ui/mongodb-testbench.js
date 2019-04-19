// MongoDB testbench, used to develop queries that are later moved to Spring Data MongoDB.

const mongoose = require('mongoose');
mongoose.connect('mongodb://localhost/alfred');
require('node-json-color-stringify');

// eslint-disable-next-line no-console
const log = console.log;
// eslint-disable-next-line no-console
const logError = console.error;

const comicSchema = mongoose.Schema({
  path     : String,
  title    : String,
  series   : String,
  volume   : String,
  number   : String,
  position : String,
  year     : Number,
  month    : Number,
  publisher: String,

  summary    : String,
  notes      : String,
  writer     : String,
  penciller  : String,
  inker      : String,
  colorist   : String,
  letterer   : String,
  coverArtist: String,
  editor     : String,
  web        : String,
  pageCount  : Number,
  manga      : Boolean,
  characters : String,
  teams      : String,
  thumbnail  : String
});

const progressSchema = mongoose.Schema({
  comicId    : String,
  userId     : String,
  read       : Boolean,
  currentPage: Number,
  lastRead   : Date
});

const Comic = mongoose.model('Comic', comicSchema, 'comic');
const Progress = mongoose.model('Progress', progressSchema, 'progress');
const userId = '104414564769351832134';
// const userId2 = '107859401383492803405';
const mockUser = 'oauth2-mock-user-id';

const progress = () => {
  return Progress.find();
};

const findAllLastReadPerVolume = () => {
  return Comic.aggregate()
    .sort({ position: 1 })
    .lookup({ from: 'progress', localField: '_id', foreignField: 'comicId', as: 'progress' })
    .replaceRoot({
      $mergeObjects: [
        { $arrayElemAt: [{ $filter: {
          input: '$progress',
          as   : 'item',
          cond : { $eq: [ '$$item.userId', userId ]}
        }}, 0 ]},
        '$$ROOT'
      ]
    })
    .project({ progress: 0, comicId: 0, userId: 0 })
    .group({
      _id       : { publisher: '$publisher', series: '$series', volume: '$volume' },
      volumeRead: { $min: { $cond: [ '$read', true, false ]}},
      readCount : { $sum: { $cond:
        [{ $or: [ '$currentPage', '$read' ]}, 1, 0 ]
      }},
      comics: { $push: '$$ROOT' }
    })
    .match({ $expr: { $eq: [ '$volumeRead', false ]}})
    .match({ $expr: { $gt: [ '$readCount', 0 ]}})
    .project({
      comics: {
        $filter: {
          input: '$comics',
          as   : 'item',
          cond : { $ne: [ '$$item.read', true ]}
        }
      }
    })
    // At this point, all we have volumes of comics that are unread.
    .replaceRoot({ $arrayElemAt: [ '$comics', 0 ]})
    .sort({ lastRead: -1 });
};

// Publishers -> Series -> Volumes
const publishers = () => {
  return Comic.aggregate()
    .lookup({ from: 'progress', localField: '_id', foreignField: 'comicId', as: 'progress' })
    .replaceRoot({
      $mergeObjects: [
        '$$ROOT',
        { $arrayElemAt: [{ $filter: {
          input: '$progress',
          as   : 'item',
          cond : { $eq: [ '$$item.userId', userId ]}
        }}, 0 ]},
        { _id: '$$ROOT._id' }
      ]
    })
    .project({ progress: 0, comicId: 0, userId: 0 })

    .sort({ position: 1 })
    .group({
      _id       : { publisher: '$publisher', series: '$series', volume: '$volume'  },
      volume    : { $last: '$volume' },
      issueCount: { $sum: 1 },
      read      : { $min: '$read' },
      readCount : { $sum: { $cond: [ '$read', 1, 0 ]}}
    })
    .group({
      _id    : { publisher: '$_id.publisher', series: '$_id.series'  },
      series : { $last: '$_id.series' },
      volumes: { $push: {
        volume    : '$volume',
        position  : '$position',
        issueCount: '$issueCount',
        readCount : '$readCount',
        read      : '$read'
      }}
    })
    .group({
      _id      : { publisher: '$_id.publisher' },
      publisher: { $last: '$_id.publisher' },
      series   : { $addToSet: { series: '$_id.series', volumes: '$volumes' }}
    })
    .sort({ publisher: 1 });
};

const findLastReadForVolume = () => {
  return Comic.aggregate()
    // should return #1 (all read)
    // .match({ publisher: 'DC Comics', series: 'Batgirl', volume: '2000' })
    // should return #2 (first read)
    .match({ publisher: 'DC Comics', series: 'Batgirl', volume: '2016' })

    .lookup({ from: 'progress', localField: '_id', foreignField: 'comicId', as: 'progress' })
    .replaceRoot({
      $mergeObjects: [
        '$$ROOT',
        { $arrayElemAt: [{ $filter: {
          input: '$progress',
          as   : 'item',
          cond : { $eq: [ '$$item.userId', mockUser ]}
        }}, 0 ]},
        { _id: '$$ROOT._id' }
      ]
    })
    .project({ progress: 0, comicId: 0, userId: 0 })

    .sort({ position: 1 })
    .sort({ read: 1 })
    .limit(1);
};

const findAllByPublisherAndSeriesAndVolumeOrderByPosition = () => {
  return Comic.aggregate()
    .match({ publisher: 'DC Comics', series: 'Batgirl', volume: '2000' })
    .lookup({ from: 'progress', localField: '_id', foreignField: 'comicId', as: 'progress' })
    .replaceRoot({
      $mergeObjects: [
        { $arrayElemAt: [{ $filter: {
          input: '$progress',
          as   : 'item',
          cond : { $eq: [ '$$item.userId', userId ]}
        }}, 0 ]},
        '$$ROOT'
      ]
    })
    .project({ progress: 0, comicId: 0, userId: 0 })

    .sort({ position: 1 });
};

const markAllAsReadUntil = () => {
  return Comic.aggregate()
    .match({ publisher: 'DC Comics', series: 'Batgirl', volume: '2000' })
    .match({ position: { $lte: '0002.0' }});
};

const db = mongoose.connection;
db.on('error', error => {
  logError(`connection error: ${ error }`);
});
db.once('open', function () {

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
