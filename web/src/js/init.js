define(['react','reactdom','jsx!/ui/header','jsx!/ui/content','jsx!/ui/footer'],function(React,ReactDOM,Header,Content,Footer) {
  ReactDOM.render(
    React.createElement(
      "div",
      null,
      React.createElement(Header, null),
      React.createElement(Content, null),
      React.createElement(Footer, null)
    ),
    document.getElementById("content")
  );
});

/*
define(['jsx!/ui/sidebar','reactdom'],
  function(SideBar,ReactDOM) {
    var sidebar = new SideBar();
    sideBar.init()
  }
);
*/