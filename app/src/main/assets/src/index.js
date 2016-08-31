var virt = require("@nathanfaucett/virt"),
    virtAndroid = require("@nathanfaucett/virt-android"),
    App = require("./App");


var socket = new WebSocket("ws://localhost:9999");


socket.onmessage = function onMessage(data) {
    socket.send(data);
};


/*
socket.onopen = function onOpen() {
    virtAndroid.render(
        virt.createView(App),
        function noop() {},
        socket,
        function attachMessage(socket, callback) {
            socket.onmessage = function onMessage(data) {
                callback(JSON.parse(data));
            };
        },
        function sendMessage(socket, data) {
            socket.send(JSON.stringify(data));
        }
    );
};
*/