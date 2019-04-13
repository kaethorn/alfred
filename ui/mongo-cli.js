const mongoose = require('mongoose');
mongoose.connect('mongodb://localhost/komix');
require('node-json-color-stringify');

const log = console.log;
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
  thumbnail  : String,
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
const userId2 = '107859401383492803405';

const findAllLastReadPerVolume = () => {
  return Comic.aggregate()
    .sort({ position: 1 })
    .lookup({ from: 'progress', localField: '_id', foreignField: 'comicId', as: 'progress' })
    .replaceRoot({
      $mergeObjects: [
        { $arrayElemAt: [{ $filter: {
          input: '$progress',
          as: 'item',
          cond: { $eq: [ '$$item.userId', userId ] }
        }}, 0 ]},
        '$$ROOT'
      ]
    })
    .project({ progress: 0, comicId: 0, userId: 0 })
    .group({
      _id       : { publisher: '$publisher', series: '$series', volume: '$volume' },
      volumeRead: { $min: { $cond: [`$read`, true, false] } },
      readCount : { $sum: { $cond:
        [{ $or: [`$currentPage`, `$read`]}, 1, 0 ]
      }},
      comics: { $push: '$$ROOT' }
    })
    .match({ $expr: { $eq: [ '$volumeRead', false ]}})
    .match({ $expr: { $gt: [ '$readCount', 0 ]}})
    .project({
      comics: {
        $filter: {
          input: "$comics",
          as: "item",
          cond: { $ne: [ `$$item.read`, true ] }
        }
      }
    })
    // At this point, all we have volumes of comics that are unread.
    .replaceRoot({ $arrayElemAt: [ '$comics', 0 ] })
    .sort({ lastRead: -1 });
};

// Volumes that have unread comics
const unreadVolumes = () => {
  return Comic.aggregate()
    .sort({ position: 1 })
    // Works:
    // .match({ 'readState.104414564769351832134': { $exists: true }})
    .project({ path: 0, notes: 0, penciller: 0, editor: 0, colorist: 0, letterer: 0, web: 0, characters: 0, teams: 0, manga: 0, inker: 0 , writer: 0, year: 0, month: 0, position: 0, thumbnail: 0, summary: 0 })
    .addFields({
      read: `$readState.${ userId }.read`,
      currentPage: `$readState.${ userId }.currentPage`,
      lastRead: `$readState.${ userId }.lastRead`
    })
    .group({
      _id       : { publisher: '$publisher', series: '$series', volume: '$volume' },
      read      : { $min: `$read` },
      lastRead  : { $max: `$lastRead` },
      readCount : { $sum: { $cond:
        [{ $or: [`$currentPage`, `$read`]}, 1, 0 ]
      }},
      comic     : { $push: '$$ROOT' }
    })
    .match({ $expr: { $eq: [ '$read', false ]}})
    .match({ $expr: { $gt: [ '$readCount', 0 ]}})
    .project({
      readCount: 1,
      read: 1,
      comic: {
        $filter: {
          input: "$comic",
          as: "item",
          cond: { $ne: [ `$$item.read`, true ] }
        }
      }
    })
    .project({
      issueCount: 1,
      readCount: 1,
      read: 1,
      comic: { $arrayElemAt: [ '$comic', 0 ] }
    })
    // At this point, all we have volumes of comics that are unread.
    .replaceRoot('$comic')
    .sort({ lastRead: -1 });
};

// Publishers -> Series -> Volumes
const publishers = () => {
  return Comic.aggregate()
    .sort({ position: 1 })
    .group({
      _id       : { publisher: '$publisher', series: '$series', volume: '$volume'  },
      volume    : { $last: '$volume' },
      issueCount: { $sum: 1 },
      read      : { $min: { $cond: [ '$readState.foo.read', 1, 0 ]}},
      readCount : { $sum: { $cond: [ '$readState.foo.read', 1, 0 ]}}
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
    .match({ publisher: 'DC Comics', series: 'Batgirl', volume: '2008' })
    .lookup({ from: 'progress', localField: '_id', foreignField: 'comicId', as: 'progress' })
    .replaceRoot({
      $mergeObjects: [
        { $arrayElemAt: [{ $filter: {
          input: '$progress',
          as: 'item',
          cond: { $eq: [ '$$item.userId', userId ] }
        }}, 0 ]},
        '$$ROOT'
      ]
    })
    .project({ progress: 0, comicId: 0, userId: 0 })

    .sort({ position: 1 })
    .match({ read: { $ne: true }})
    .limit(1)
};

const findAllByPublisherAndSeriesAndVolumeOrderByPosition = () => {
  return Comic.aggregate()
    .match({ publisher: 'DC Comics', series: 'Batgirl', volume: '2000' })
    .lookup({ from: 'progress', localField: '_id', foreignField: 'comicId', as: 'progress' })
    .replaceRoot({
      $mergeObjects: [
        { $arrayElemAt: [{ $filter: {
          input: '$progress',
          as: 'item',
          cond: { $eq: [ '$$item.userId', userId ] }
        }}, 0 ]},
        '$$ROOT'
      ]
    })
    .project({ progress: 0, comicId: 0, userId: 0 })

    .sort({ position: 1 })
};

const markAllAsReadUntil = () => {
  return Comic.aggregate()
    .match({ publisher: 'DC Comics', series: 'Batgirl', volume: '2000' })
    .match({ position: { $lte: '0002.0'}});
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
    case 'unread':
    case 'u':
      method = unreadVolumes;
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
      log(res);
      // log(JSON.colorStringify(res, null, 2));
      log(`Result length: ${ res.length }`);
      mongoose.connection.close();
    });
});
