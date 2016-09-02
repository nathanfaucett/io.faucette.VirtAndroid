var virt = require("@nathanfaucett/virt");


var AppPrototype;


module.exports = App;


function App(props, children, context) {

    virt.Component.call(this, props, children, context);

    this.state = {
        count: 0
    };
}
virt.Component.extend(App, "example.App");
AppPrototype = App.prototype;

AppPrototype.componentDidMount = function() {
    var _this = this;

    setTimeout(function onSetTimeout() {
        _this.replaceState({
                count: _this.state.count + 1
            },
            function onReplaceState() {
                setTimeout(onSetTimeout, 1000);
            }
         );
    }, 1000);
};

AppPrototype.render = function() {
    return (
        virt.createView("TextView", this.state.count)
    );
};
