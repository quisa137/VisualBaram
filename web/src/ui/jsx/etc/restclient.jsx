define(['react','reactdom','jsx!/ui/util/ajaxRequest'],function(React,ReadDOM,AjaxRequest) {
  class ReactClient extends React.Component {
      constructor(props) {
          super(props);
          this.displayName = 'ReactClient';
          this.state = {url:'',requestBody:''}
      }
      render() {
          return (
            <div>
              <input type="text" value={this.state.url}/>
              <textarea name="" id="" value={this.state.requestBody}/>
            </div>
          );
      }
  }

  return ReactClient;

});
