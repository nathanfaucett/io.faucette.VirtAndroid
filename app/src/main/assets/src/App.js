var virt = require("@nathanfaucett/virt");


var AppPrototype;


module.exports = App;


function App(props, children, context) {
    var _this = this;

    virt.Component.call(this, props, children, context);

    this.state = {
        count: 0
    };

    this.onClick = function(e) {
        return _this.__onClick(e);
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

AppPrototype.__onClick = function(e) {
    this.replaceState({
        count: this.state.count + 1
    });
};

AppPrototype.render = function() {
    return (
        virt.createView("android.widget.LinearLayout", {
                layout_width: "match_parent",
                layout_height: "match_parent",
                padding: "16 16 16 16"
            },

            virt.createView("android.widget.TextView", {
                layout_width: "wrap_content",
                layout_height: "wrap_content",
                text: this.state.count
            }),

            virt.createView("android.widget.Button", {
                onClick: this.onClick,
                layout_width: "wrap_content",
                layout_height: "wrap_content",
                text: "Count!"
            })
        )
    );
};
