# Java Ristretto 

Java Ristretto is a set of plugins that changes how Java code is compiled in order to reduce boilerplate.

## Default Immutability

### Parameters

<table>
<tr>
<th>Source</th><th>Generated</th>
</tr>
<tr>
<td>
<pre>
class MyClass {
  void doSomething(String value) {
    // ...
  }
}
</pre>
</td>
<td>
<pre>
class MyClass {
  void doSomething(<strong>final</strong> String value) {
    // ...
  }
}
</pre>
</td>
</tr>
</table>

### Variables

<table>
<tr>
<th>Source</th><th>Generated</th>
</tr>
<tr>
<td>
<pre>
class MyClass {
  void doSomething() {
    String value = "Hello World";
  }
}
</pre>
</td>
<td>
<pre>
class MyClass {
  void doSomething() {
    <strong>final</strong> String value = "Hello World";
  }
}
</pre>
</td>
</tr>
</table>

### Fields

<table>
<tr>
<th>Source</th><th>Generated</th>
</tr>
<tr>
<td>
<pre>
class MyClass {
  String value;
}
</pre>
</td>
<td>
<pre>
class MyClass {
  <strong>final</strong> String value;
}
</pre>
</td>
</tr>
</table>

## Default toString Implementation (TODO)

<table>
<tr>
<th>Source</th><th>Generated</th>
</tr>
<tr>
<td>
<pre>
class MyClass {
  String value;
}
</pre>
</td>
<td>
<pre>
class MyClass {
  String value;
  <strong>@Override
  public String toString {
    return "MyClass[value=" + value + "]";
  }</strong>
}
</pre>
</td>
</tr>
</table>

## Default equals/hashCode Implementation (TODO)

<table>
<tr>
<th>Source</th><th>Generated</th>
</tr>
<tr>
<td>
<pre>
class MyClass {
  String value;
}
</pre>
</td>
<td>
<pre>
class MyClass {
  String value;
  <strong>@Override
  public int hashCode() {
    return ...;
  }
  @Override
  public boolean equals(Object o) {
    return ...;
  }</strong>
}
</pre>
</td>
</tr>
</table>

## Default Constructor (TODO)

<table>
<tr>
<th>Source</th><th>Generated</th>
</tr>
<tr>
<td>
<pre>
class MyClass {
  String value;
}
</pre>
</td>
<td>
<pre>
class MyClass {
  <strong>private</strong> <strong>final</strong> String value;
  <strong>public MyClass(final String value) {
    if (value == null) {
      throw new NullPointerException("...");
    }
    this.value = value;
  }</strong>
}
</pre>
</td>
</tr>
</table>

## Default Visibility

### Fields

<table>
<tr>
<th>Source</th><th>Generated</th>
</tr>
<tr>
<td>
<pre>
class MyClass {
  String value;
}
</pre>
</td>
<td>
<pre>
class MyClass {
  <strong>private</strong> String value;
}
</pre>
</td>
</tr>
</table>

### Methods (TODO)

<table>
<tr>
<th>Source</th><th>Generated</th>
</tr>
<tr>
<td>
<pre>
class MyClass {
  void doSomething() {
  }
}
</pre>
</td>
<td>
<pre>
class MyClass {
  <strong>public</strong> void doSomething() {
  }
}
</pre>
</td>
</tr>
</table>

### Constructors (TODO)

<table>
<tr>
<th>Source</th><th>Generated</th>
</tr>
<tr>
<td>
<pre>
class MyClass {
  MyClass() {
  }
}
</pre>
</td>
<td>
<pre>
class MyClass {
  <strong>public</strong> MyClass() {
  }
}
</pre>
</td>
</tr>
</table>

### Classes (TODO)

<table>
<tr>
<th>Source</th><th>Generated</th>
</tr>
<tr>
<td>
<pre>
class MyClass {
}
</pre>
</td>
<td>
<pre>
<strong>public</strong> class MyClass {
}
</pre>
</td>
</tr>
</table>

## Properties (TODO)

### Read-Only

<table>
<tr>
<th>Source</th><th>Generated</th>
</tr>
<tr>
<td>
<pre>
class MyClass {
  public String value;
}
</pre>
</td>
<td>
<pre>
class MyClass {
  <strong>private</strong> <strong>final</strong> String value;
  <strong>public String value() {
    return value;
  }</strong>
}
</pre>
</td>
</tr>
</table>

### Read-Write

<table>
<tr>
<th>Source</th><th>Generated</th>
</tr>
<tr>
<td>
<pre>
class MyClass {
  @Mutable
  public String value;
}
</pre>
</td>
<td>
<pre>
class MyClass {
  <strong>private</strong> String value;
  <strong>public String value() {
    return value;
  }
  public MyClass value(final String value) {
    if (value == null) {
      throw new NullPointerException("...");
    }
    this.value = value;
    return this;
  }</strong>
}
</pre>
</td>
</tr>
</table>
