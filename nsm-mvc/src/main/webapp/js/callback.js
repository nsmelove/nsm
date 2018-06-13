/**
 * Created by nieshuming on 2018/6/12.
 */
function print1(num, p){
    if(num == 1) {
        console.log(num);
        return num;
    }else {
        if(p) {
            return p(num, print6);
        }else {
            return print6(num);
        }
    }
}

function print2(num, p){
    if(num == 2) {
        console.log(num);
        return num;
    }else {
        if(p) {
            return p(num, print1);
        }else {
            return print1(num);
        }

    }
}

function print3(num, p){
    if(num == 3) {
        console.log(num);
        return num;
    }else {
        return p(num);
    }
}

function print4(num, p){
    if(num == 4) {
        console.log(num);
        return num;
    }else {
        if(p) {
            return p(num, print3);
        }else {
            return print3(num);
        }
    }
}

function print5(num, p){
    if(num == 5) {
        console.log(num);
        return num;
    }else {
        if(p) {
            return p(num, print4);
        }else {
           print4(num);
        }
    }
}

function print6(num, p){
    if(num == 6) {
        console.log(num);
        return num;
    }else {
        if(p) {
            return p(num, print5);
        }else {
            print5(num)
        }

    }
}

function doSomething(cb){
    console.log("do something");
    cb(afterOther);
    function afterOther(cb){
        console.log("afterOther do something");
        cb(afterAfterOther);
        function afterAfterOther(cb){
            console.log("afterOther do something");
        }
    }
}

function doOther(cb){
    console.log("do other");
    doSomething(afterSomething);
    function afterSomething(cb){
        console.log("afterSomething do other");
        cb();
    }
}

doSomething(doOther);