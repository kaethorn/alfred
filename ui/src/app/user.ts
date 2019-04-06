export interface UserAuthority {
  authority: string;
}

export interface UserDetails {
  remoteAddress: string;
  sessionId: string;
  tokenValue: string;
  tokenType: string;
  decodedDetails: object;
}

export interface UserAuthenticationDetails {
  id: string;
  email: string;
  verified_email: boolean;
  name: string;
  given_name: string;
  family_name: string;
  link: string;
  picture: string;
  locale: string;
}

export interface UserAuthentication {
  authorities: UserAuthority[];
  details: UserAuthenticationDetails;
  authenticated: boolean;
  principal: string;
  credentials: string;
  name: string;
}

export interface Oauth2Request {
  clientId: string;
  scope: object[];
  requestParameters: object;
  resourceIds: string[];
  authorities: string[];
  approved: boolean;
  refresh: boolean;
  redirectUri: string;
  responseTypes: string[];
  extensions: object;
  refreshTokenRequest: object;
  grantType: string;
}

export interface User {
  authorities: UserAuthority[];
  details: UserDetails;
  authenticated: boolean;
  userAuthentication: UserAuthentication;
  credentials: string;
  principal: string;
  clientOnly: boolean;
  oauth2Request: Oauth2Request;
  name: string;
}
