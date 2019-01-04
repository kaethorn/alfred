import { InjectableRxStompConfig } from '@stomp/ng2-stompjs';

export const RxStompConfig: InjectableRxStompConfig = {
  brokerURL: 'ws://localhost:8080/scanner/websocket',

  connectHeaders: {},

  heartbeatIncoming: 0,
  heartbeatOutgoing: 20000,

  reconnectDelay: 200,

  // Will log diagnostics on console
  // It can be quite verbose, not recommended in production
  // Skip this key to stop logging to console
  // debug: (msg: string): void => {
  //   console.log(new Date(), msg);
  // }
};
