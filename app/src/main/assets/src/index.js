var virtAndroid = require("@nathanfaucett/virt-android");


var virt = virtAndroid.virt,
    socket = new WebSocket("ws://localhost:9999");


socket.onopen = function onOpen() {
    virtAndroid.render(
        virt.createView("View", {
            property: "value"
        }, "Hello, world!"),
        function noop() {},
        socket,
        function attachMessage(socket, callback) {
            socket.onmessage = function onMessage(e) {
                callback(JSON.parse(e.data));
            };
        },
        function sendMessage(socket, data) {
            socket.send(JSON.stringify(data));
        }
    );
};
