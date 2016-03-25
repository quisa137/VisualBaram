/**
 각 플러그인들을 requirejs에 맞게 수정하였음, 아래는 수정 내역
 - JSX : babelSync,babelAsync의 babel의 Option에 presets를 추가함
 - fetch : self.fetch가 존재하면, return self.fetch 하도록 수정함
semantic-ui는 별다른 설정 없이도 React와 호환가능
http://semantic-ui.com/introduction/integrations.html
*/
require.config({
  baseUrl: '/',
  paths:{
    'react':'/modules/react/react-with-addons',
    'reactdom':'/modules/react/react-dom',
    'fetch':'/modules/fetch/fetch',
    'redux':'/modules/redux/redux.min',
    'jquery':'/modules/jquery/jquery-1.12.1.min',
    'babel':'/modules/babeljs/babel.min',
    'semantic':'/modules/semantic-ui/semantic.min',
    'jsx':'/modules/requirejs-react-jsx/jsx',
    'text':'/modules/requirejs/text',
    'lodash':'/modules/lodash/lodash',
    'd3':'/modules/d3/d3.min',
    'moment':'/modules/moment/moment-with-locales',
    'bluebird':'/modules/bluebird/bluebird.core.min'
  },
  //shim은 amd또는 commonJS의 방식을 지원하지 않는 라이브러리를
  //requirejs에서 지원하기 위해 들어가는 설정이다.
  //amd나 commonJS는 자바스크립트 모듈을 만드는 방식에 관한 스펙이다.
  //두가지인 이유는 표준 위원회에서 정하다 보니 의견이 갈라진 거고
  //어느방식을 쓰더라도 어느정도 지원해준다. 여기서는 amd의 방식을 쓴다.
  shim : {
    'bluebird':{exports:'Promise'},
    'semantic': {
      deps:['jquery'],
      'exports':'semantic'
    },
    'fetch': {
      deps:['bluebird'],
      'exports':'fetch'
    }
  },
  config: {
    babel: {
      sourceMaps: 'inline', // One of [false, 'inline', 'both']. See https://babeljs.io/docs/usage/options/
      presets: ['es2015','react'],
      fileExtension: '.jsx' // Can be set to anything, like .es6 or .js. Defaults to .jsx
    }
  },
  deps:['bluebird','react','reactdom','jquery','babel'],
  callback:function(){
    /* VisualBaram Entry Point */
    requirejs(['/static/init.js']);
    /* Promise 객체를 전역 변수로 선언(브라우저 이슈 관련) */
    window.Promise = require('bluebird');
  }
});

