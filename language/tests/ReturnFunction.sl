function retfun(f) {
    return f;
}

function add(a, b) {
    return a + b;
}

function mul(a, b) {
    return a * b;
}

function main() {
    fun = retfun(mul);
    println(fun(3, 4));
}
