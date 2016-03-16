/**
 각 플러그인들을 requirejs에 맞게 수정하였음, 아래는 수정 내역
 - JSX : babelSync,babelAsync의 babel의 Option에 presets를 추가함
semantic-ui는 별다른 설정 없이도 React와 호환가능
http://semantic-ui.com/introduction/integrations.html
*/
require.config({
  baseUrl: '/',
  paths:{
    "react":"/modules/react/react-with-addons.min",
    "reactdom":"/modules/react/react-dom.min",
    "jquery":"/modules/jquery/jquery-1.12.1.min",
    "babel":"/modules/babeljs/babel.min",
    "semantic":"/modules/semantic-ui/semantic.min",
    "jsx":"/modules/requirejs-react-jsx/jsx",
    "text":"/modules/requirejs/text",
    "lodash":"/modules/lodash/lodash",
    'd3':"/modules/d3/d3.min"
  },
  shim : {
    "react": {
      "exports": "React"
    },
    "babel": {
      "exports":"Babel"
    },
    "jquery": {
      "exports":"jQuery"
    },
    "lodash": {
      "exports":"lodash"
    },
    "d3": {
      "exports":"d3"
    },
    "semantic": {
      deps:['jquery'],
      "exports":"semantic"
    }
  },
  config: {
    babel: {
      sourceMaps: "inline", // One of [false, 'inline', 'both']. See https://babeljs.io/docs/usage/options/
      presets: ['es2015','react'],
      fileExtension: ".jsx" // Can be set to anything, like .es6 or .js. Defaults to .jsx
    }
  },
  deps:['react','reactdom','jquery','babel']
});

/* VisualBaram Entry Point */
requirejs(['/static/init.js']);