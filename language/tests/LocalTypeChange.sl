def recursion(n) {
  local = 42;
  
  if (n > 0) {
    recursion(n - 1);
  } else {
    local = "abc";
  }
  
  println(local);
}

def main() {
  recursion(3);
}  
