define(['react'],
  function(React) {
    class HeaderModule extends React.Component {
      constructor() {
        super();
      }
      componentDidMount() {
      }
      render() {
        return (
          <div className="ui fixed inverted yellow menu">
            <div className="ui container">
              <a href="#" className="header item">
                <i className="arrow right icon"></i>
                Baram Home
              </a>
              <a href="http://localhost:8081/Monitoring" className="item">Monitoring</a>
              <div className="ui simple dropdown item">
                Visualization <i className="dropdown icon"></i>
                <div className="menu">
                  <a className="item" href="http://localhost:8081/Streaming" target="_self">Streaming</a>
                  <a className="item" href="http://localhost:8081/Interest" target="_self">Bar</a>
                  <a className="item" href="http://localhost:8081/Advertising" target="_self">Line</a>
                  <a className="item" href="http://localhost:8081/InstallEnv" target="_self">Circle1</a>
                  <a className="item" href="http://localhost:8081/FirstImpression" target="_self">Circle2</a>
                  <a className="item" href="http://localhost:8081/Population" target="_self">Heatmap</a>
                  <a className="item" href="http://localhost:8081/Economy" target="_self">Range</a>
                  <a className="item" href="http://localhost:8081/Network" target="_self">Network</a>
                </div>
              </div>
            </div>
          </div>
        );
      }
    }
    return HeaderModule;
  }
);