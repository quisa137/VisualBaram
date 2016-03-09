define(['react','reactdom','semantic','jquery'],
  function(React,ReactDOM,semantic,$){
    "use strict";
    class SideBarModule extends React.Component {
      constructor() {
        super();
      }
      init() {
        ReactDOM.render(
          <SideBarModule />,
          $('#content')[0]
        );
      }
      componentDidMount() {
        $('.ui.sidebar')
          .sidebar({
            context: $('.bottom.segment')
          })
          .sidebar('attach events', '.menu .item');
      }
      render() {
        return (
        <div>
          <div className="ui top attached demo menu">
            <a className="item">
              <i className="sidebar icon"></i>
              Baram
            </a>
          </div>
          <div className="ui bottom attached segment pushable">
            <div className="ui inverted labeled icon left inline vertical sidebar menu">
              <a className="item">
                <i className="home icon"></i>
                Home
              </a>
              <a className="item">
                <i className="block layout icon"></i>
                Topics
              </a>
              <a className="item">
                <i className="smile icon"></i>
                Friends
              </a>
              <a className="item">
                <i className="calendar icon"></i>
                History
              </a>
            </div>
            <div className="pusher">
              <div className="ui basic segment">
                <h3 className="ui header">Application Content</h3>
                <p></p>
                <p></p>
                <p></p>
                <p></p>
              </div>
            </div>
          </div>
        </div>
        );
      }
    }
    return SideBarModule;
  }
);
