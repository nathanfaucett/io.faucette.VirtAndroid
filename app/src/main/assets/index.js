var math = require("./math");


console.log(math.add(1, 1));
setImmediate(function() {
    console.log(math.add(2, 2));
});
console.log("Sync");