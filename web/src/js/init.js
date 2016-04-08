/**
 * 제일 처음 실행되는 스크립트
 * 여기서 사이트 공통 데이터 스토리지를 정의한다.
 * 네비게이션 매니져, 데이터 로더, 출력 담당등의 객체들이 생성,
 * 컨텍스트에 등록되어야 한다.
 * 사이트 설정 처리, 룰 정의를 실행한다.
 * 이곳에서 레이아웃 구조를 정한다.
 * Header Content Footer의 순서이다.
*/
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