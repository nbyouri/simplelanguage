def invoke(f) {
  f("hello");
}

def f1() {
  println("f1");
}

def f2() {
  println("f2");
}

def f3() {
  println("f3");
}

def f4() {
  println("f4");
}

def f5() {
  println("f5");
}

def main() {
  invoke(f1);
  invoke(f2);
  invoke(f3);
  invoke(f4);
  invoke(f5);
  invoke(foo);  
}  
