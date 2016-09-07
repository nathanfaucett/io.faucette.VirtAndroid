var virt = require("@nathanfaucett/virt"),
    virtAndroid = require("@nathanfaucett/virt-android"),
    App = require("./App");


var socket = new WebSocket("ws://localhost:9999");


socket.addEventListener("open", function onOpen() {
    virtAndroid.render(
        virt.createView(App),
        function onFirstRender() {},
        socket,
        function attachMessage(socket, callback) {
            socket.addEventListener("message", function onMessage(e) {
                callback(JSON.parse(e.data));
            });
        },
        function sendMessage(socket, data) {
            socket.send(JSON.stringify(data));
        }
    );
});