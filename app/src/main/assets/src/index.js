var isNull = require("@nathanfaucett/is_null/src/index"),
    math = require("./math");


console.log(math.add(1, 1));
setImmediate(function() {
    console.log(math.add(2, 2));
});
console.log("Sync");

var id = setImmediate(function() {
    console.log("Never called");
});
clearTimeout(id);

console.log("this should be false " + isNull(id));
console.log("id " + id + " will never be called");