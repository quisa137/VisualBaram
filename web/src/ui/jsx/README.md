JSX은 성능문제로 라이브버전에서는 컴파일하여 JS파일로 변경하여야 한다.
개발환경에서는 현재 파일로 클라이언트에 던져준다. 코드 스타일은
https://github.com/airbnb/javascript/tree/master/react의 스타일을 쓰기로 한다.

h2. React가 호출하는 이벤트 핸들링 메소드
 * componentWillMount : 아래의 태그들이 DOM에 등록되기 전에 한번 호출된다.
componentDidMount : 아래의 태그들이 DOM에 등록된 후 한번 호출된다.
componentWillReceiveProps : 현재의 클래스에 새로운 props를 받았을 때 호출됨, 최초 랜더링 시에는 호출되지 않음
shouldComponentUpdate : 새로운 prop 또는 state를 받아 렌더링 하기전에 호출됨, 최초 렌더링 시나 forceUpdate를 사용하는 경우에는 호출안됨
componentWillUpdate : 새로운 prop 또는 state를 받아 렌더링 하기 직전에 호출됨, 최초 렌더링 시에는 오출되지 않음, this.setState를 호출할 수 없음
componentDidUpdate : 컴포넌트의 없데이트가 DOM에 반영된 직후에 호출됨, 최초렌더링 시에는 호출되지 않음
componentWillUnmount : 컴포넌트가 DOM에서 마운트 해제되기 직전에 호출, 타이머를 무효화하거나 componentDidMount 에서 만들어진 태그들을 정리하는 데 사용

http://mobicon.tistory.com/471 - 쓸만한 내용
https://docs.google.com/presentation/d/1m_vVivj1fxyLQHPJnemR9etZZPGIX1Fshw8zbh9ZwsU/edit#slide=id.g5a5bd3cbf_0_38 - AngularJS와 React의 비교