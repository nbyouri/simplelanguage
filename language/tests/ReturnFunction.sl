def retfun(f) {
    return f;
}

def add(a, b) {
    return a + b;
}

def mul(a, b) {
    return a * b;
}

def main() {
    fun = retfun(mul);
    println(fun(3, 4));
}
