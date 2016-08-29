var virt = require("@nathanfaucett/virt"),
    math = require("./math");


console.log(math.add(1, 1));
setImmediate(function() {
    console.log(math.add(2, 2));
});
console.log("sync");


var id = setImmediate(function() {
    console.log("Never called");
});
clearTimeout(id);


console.log("id " + id + " will never be called");


var now = Date.now();
process.nextTick(function onNextTick() {
    console.log(Date.now() - now);
});
