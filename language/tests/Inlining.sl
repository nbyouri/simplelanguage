
def a() {return 42;}
def b() {return a();}
def c() {return b();}
def d() {return c();}
def e() {return c();}
def f() {return c();}
def g() {return d() + e() + f();}

def main() {
    i = 0;
    result = 0;
    while (i < 10000) {
        result = result + g();
        i = i + 1;
    }
    return result;
}
