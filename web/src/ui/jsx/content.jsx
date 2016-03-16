define(['react','semantic','jquery'],
  function(React,semantic,$) {
    class ContentModule extends React.Component {
      render() {
        return (
        <div className="ui main text container">
          <form method="POST" className="ui form">
            <div className="field">
              <label htmlFor="">URI</label>
              <input type="text" name="uri" value=""/>
            </div>
            <div className="field">
              <label htmlFor="">SearchData</label>
              <textarea name="searchOption" id=""></textarea>
            </div>
          </form>
        </div>
        );
      }
    }
    return ContentModule;
  }
);