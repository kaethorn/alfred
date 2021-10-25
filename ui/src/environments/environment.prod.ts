import packageJson from '../../package.json';
const version = packageJson.version;

export const environment = {
  production: true,
  version
};
