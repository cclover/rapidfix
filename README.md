# rapidfix
这个项目是一种不需要重启APP修改程序方法。这也只是个Demo，提供一种思路。 和Instant Run实现的方式是一样的。


## 常见热修复思路

目前Android上热修复的思路基本都是利用ClassLoader加载的原理，用新的class替换老的同名class，这种方式的确定是进程需要关闭重启。还有其他类似腾讯Tinker的替换dex(dex文件合并)的方式。但是总的思路还是抛弃来的class，利用新的。

而之前阿里的Andfix的思路不太一样，是在native层修改，以ART为例。每个Class对象都有ArtMethod List，这个List中存放的是Class定义的所有方法，用ArtMethod表示。而ArtMethod并不是真正存放代码的地方，他通过一些entrypoint来指真正machine code或者是dalvik bytecode。Andfix并没有也不可能干掉VM中的Class和它的ArtMethod对象，他们也是通过Classloader加载了自己新的class，然后在native层，通过修改ArtMethod的entrypoint的方式，让他指向新的ArtMethod的code address达到了动态修复的目的。

这是一个比较好的思路。所以我在思考Java层能否也做类似的事情，这样可以最大限度的提高兼容性。因为修改Native层会随着 Android VM版本的变化而变化，而且各个厂商也可能会修改，不过实际上大多厂商不太会去修改VM方法执行的flow，起码我们之前是没有修改过。


## 新思路

Native层进行修改，Java层可以吗？ ART中Java层的Class，Method， Field内在VM的 mirror目录下都有对应的c++类，而c++的类的字段，在Java中都有

```java
public final class Class<T> implements Serializable, AnnotatedElement, GenericDeclaration, Type {

    private static final long serialVersionUID = 3206093459760846163L;

    /** defining class loader, or null for the "bootstrap" system loader. */
    private transient ClassLoader classLoader;

    /** access flags; low 16 bits are defined by VM spec */
    private transient int accessFlags;

    /** static, private, and &lt;init&gt; methods. */
    private transient long directMethods;
    
    /** Virtual methods defined in this class; invoked through vtable. */
    private transient long virtualMethods;
}
```
看看关键代码，有directMethods和virtualMethods 都是long形，就是方法表的指针，把这个修改成新的class的是不是就OK了？尝试反射去那一下这2个字段，结果出错，提示找不到。在看看，都有transient关键字。应该是这个导致C++层的字段不能序列化，不能和Java层映射起来。猜测是这样，实际不是很确定。所以没有办法。此路不通。


## 在换个思路

要想从Java层修改VM的东西看来是走不通了。突然想到代理模式。既然原来的类要被新的类替换必须重启，要实现不重启修复，就不能放弃掉来的类。既然不能抛弃，那就利用起来，让老的类变成一个代理类，正常情况调用自己的方法，当有了patch的时候，自己变成代理类调用patch中的方法。


### 原理

要怎么才能让他实现这样的功能，需要一个触发条件，if xxx就执行原有代码， else 就执行新的

#### 原始的类
```java

class Test {
  
  private int add(int x, int y){
    return x + y;
  }

}
```

#### 需要支持动态修复，修改后类

```java

class Test {
  
  private static Test stub = null;
  
  private int add(int x, int y){
    if(stub != null){
      return stub.add(x, y);
    }
    return x + y;
  }

}

```

增加一个同类型的静态字段，每个方法都要埋入一段逻辑，stub字段为null，这个时候执行 x+y的方法，如果不为null就执行stub的add方法。这里可能有以为，执行Test的add方法，那不是一样了吗？没区别啊？？ 


#### 是否可行？

这里就是关键，我们这个Test stub指向的并不是当前的Test对象，而是修改后的Test类。这样就OK了？


结论是不一定，因为如果我们patch中也包含一个Test类，我们使用DexClassLoader，一般会把系统的PathClassLoader指定为他的parent class loader，这样用DexClassLoader去loadclass的时候会先委托父类去找Test类，结果就找到系统中老的Test类了。而且会造成嵌套调用死循环。那么我们在创建DexClassLoader的时候只能传递PathClassLoader的parent classloader，这样才能加载到我们新的Test类。

还有一种方式，Java是面向对象的语言，最大的特点是什么？？多态，一切都是虚方法。 所以我们也可以利用多态的方式来动态改变stub.add方法的行为，这样就要使用不同名字的类

```java

class TestModify extend Test {
  
  private int add(int x, int y){

    return x + y + 1; // modify
  }

}

```
这样我们创建了一个Test的子类，TestModify。并生成patch.jar


#### 加载patch

有了上面的基础，我们在运行时如果检测到了patch.jar， 就新建一个DexClassLoader加载这个dex文件。然后通过反射创建一个TestModify的实例对象。在通过反射获得Test类的静态字段stub，并把这个TestModify的实例设置到stub上，结果就会变成：

```java

class Test {
  
  private static Test stub = new TestModify();
  
  private int add(int x, int y){
    if(stub != null){
      return stub.add(x, y);  //run this
    }
    return x + y;
  }

}

```
最终执行TestModify的add方法，达到动态修改程序逻辑的目的。这样还有一个好出，一旦patch有问题，可以很容易的回滚。


### 项目运行
上传的Demo编译后可以直接运行。点击Test，然后Getpatch 然后apply ，在点击Test马上就生效

![image](https://github.com/cclover/store/blob/master/images/Screenshot_1481380893.png)
![image](https://github.com/cclover/store/blob/master/images/Screenshot_1481380898.png)
![image](https://github.com/cclover/store/blob/master/images/Screenshot_1481380902.png)


### 项目编译

正常直接编译就可以了，如果要生成patch，因为我没有为这个写脚本，所以需要手动进行。 
* 1.为你要修改的类创建一个子类，并修改子类的名字为name$override，也就是在原有名字后加上$overrid。
* 2.启用instantlib下的PatchesLoaderImpl类，并在list数组中填入要修改类的全名
* 3.重新编译程序，把新的子类的class和PatchesLoaderImpl.class提取出来，注意要按照package来创建对应文件夹放class，否则后面无法dx成功。
* 4.使用jar -cvf 命令打包成class的jar
* 5.使用dx --dex --output命令转换为dex的jar
* 6.把patch.jar放到程序的files目录下就可以测试了。


## 问题

研究一个技术的原理总是很简单，但是当要产品话，运用到实际的项目中，还有很长的一段路。不光要考虑怎么实现，还要考虑实际使用中代码的管理，patch的生成发布等等。所以存在一些问题：

* 如何埋入需要的代码？
这是摆在最首位的问题，如果是新代码还好，但是如果是既有代码，每个方法前面都要加入，必定是一个浩大的工程。所以还是考虑编写gradle插件自动执行，在hotfix中已经有利用脚本在构造函数中插入代码的先例了，所以也不算是很难解决的问题了。

* 如果修改文件生成patch？
就是在原有代码基础上生成一个子类，然后打包成dex，这个并没有什么难度。但是有一点要考虑，我通过新建子类的方式修改，在下一次发布版本时，怎么把子类的代码合并到父类，这也是需要考虑的。所以是否也可以利用gradle插件在编译，自动修改类的名字和父类，这样既可以在原有类上修改，或者按前面提到的，用相同的类名的方式实现？

* 混淆的处理？
因为混淆会导致类名，方法名变化，所以要保证APK和patch中的类的方法名混淆后是一致的，所以要在每次混淆前apply上一次的mapping.txt，那么保存mapping.txt和下次自动apply也需要通过脚本实现。

* multi-dex问题？
从原理来看，我们是利用反射方式，和multi dex并没有太大关系，所以没有问题

* 4.x是否也会有CLASS_ISPREVERIFIED问题
经过测试没有这个问题，理论上来说应该也不会有问题。因为A如果直接引用B，而B不和A在同一个dex中会出现问题，但是我们这种方式都没有直接引用子类对象，而是通过父类多态的方式引用。所以应该不会有问题。如果我们采用同名类，这个不确定会不会有问题。CLASS_ISPREVERIFIED这里代码需要在仔细看看。


* 类状态怎么办？
这是这种方式碰到的最大的问题，A类的方法可以动态修改成新的A子类的方法，但是A的成员变量值无法保存到新的对象中。最直观的想法，每个class都实现深拷贝，然后把变量值拷贝过去。但是这个不现实，因为不可能知道有多少个实例对象，而且就算知道，深拷贝所有字段，代价也很大。 有没有更好的办法？ 那就是调用方式时让以前的类告诉我们他的值。也就是在stub调用add方法时，多传递一个this对象，而子类，每个方法增加一个参数接收，这样子类要使用变量时就通过传递的this指针去访问。

* 构造函数怎么修改？
因为没有办法在父类调用子类构造函数，所以一个解决方法就是构造函数逻辑全部放到一个单独的函数中，最终修改这个函数。

* super调用父类方法怎么办？
如果修改的方法通过super调用了父类的方法，我们直接修改，新的子类会不停调用父类，导致over flow，所以目前只能在新的子类中不用super调用，而老的类插入的代码必须插入在super调用之后


## 后记
所以这个方案还是有不少问题，Demo也只是在一个很初始的状态。不过思路是不错的。如果你属性Android Studio的Instant Run原理，那么你会发现，两者思想基本是一样的。不同的是这里使用继承的方式。而InstantRun是一个全新的类。 后来我也是利用了Instant完全一样的原理，写了一套新的InstantFix框架，并且踩了很多坑，验证了混淆、multi-dex、爱加密这些情况，还有实际使用中修改，发布流程。其实最复杂的倒不是怎么打patch，而是要写gradle插件来插入代码，生成patch。 因为这个后面会引用到产品中，所以现在是不会开源。但是有了这个原理，并且InstantRun的原理通过看Android gradle的源码可以搞清楚，所以其实也不是很复杂的东西。
