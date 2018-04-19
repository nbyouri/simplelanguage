def invoke(f) {
  f("hello");
}

def f1() {
  println("f1");
}

def main() {
  invoke(f1);
  invoke(foo);  
}  
