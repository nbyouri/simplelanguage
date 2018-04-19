def add(a, b) {
  return a + b;
}

def sub(a, b) {
  return a - b;
}

def foo(f) {
  println(f(40, 2));
}

def main() {
  foo(add);
  foo(sub);
}  
