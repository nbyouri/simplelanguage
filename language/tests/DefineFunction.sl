def foo() {
  println(test(40, 2));
}

def main() {
  defineFunction("def test(a, b) { return a + b; }");
  foo();

  defineFunction("def test(a, b) { return a - b; }");
  foo();
}  
