# 第五章 初始化与清理
## 重载方法的原因
- java中的构造器使用了与类名一样的方法名，所以如果不用重载，那类就只有一种构造方
式了（C语言中每个方法必须要有不同的标识符）
- 同一个词赋予不同的含义

## 清理：终结处理和垃圾回收

- **垃圾回收准备回收一个对象时，先会调用finalize()（我把它理解为试探）,然后在下次执行回收动作的时候开始回
收(可能回收失败，如果他被别的对象引用)，所以可以在finalize中做清理工作。**
- 垃圾回收机只能回收new出来的对象，对于其他方式开辟的内存（**java中一切皆对象，所以其他方式指的就是C方法**），比如通过C创建的，就没法
回收，所以可以在finalize中调用响应的方法去回收。

>例如，假设某一个对象在创建过程中会将自己绘制到屏幕上，如果不是明确地从屏幕上将其擦出，它可能永远都不会被清理。如果在finalize()加入某一种擦除功能，当GC工作时，finalize()得到了调用，图像就会被擦除。要是GC没有发生，那么这个图像就会被一直保存下来。

 对于这段话，我的理解是，一个对象将自己绘制到屏幕上，那么“屏幕”将会持有他的引用，如果没有擦除，
 那么这个 引用一直存在，那么这个对象不会被清除，因为回收机想要回收他的时候，发现他被引用了。这里需要说下回收机的机制：他首先会“试探”某个对象，告诉他，你要被回收，并且回调他的finalize方法，然后下次执行操作的时候，才会真正执行回收操作（不一定可以被回收）
 所以，在finalize中擦除（清空这个引用），然后下次就可以被回收了。

 不要太依赖finalize，而是创建其他清理方法，清理如上那种情况中的擦除。数据库连接的关闭，文件流的关闭等都是这个道理。

#### 垃圾回收思想

- 从存活的堆栈（方法栈等）或者静态存储区开始沿着引用链走，能找到的就是活的对象。这样可以解决传统的利用计数的方式来实现垃圾回收的方式(事实上没有虚拟机会这样干)。

- 通过上述方法，就找到了存活的对象，然后就可以回收无用的对象了，这里的处理方法有多种，各个虚拟机不同
    - “停止-复制”法，首先暂停程序的运行，然后将存活的对象复制到另一个堆中（按顺序整齐的排列起来，这样就实现了内存碎片的整理），剩余的对象就是需要被回收的对象。该方法的处理效率低，主要有两个因素：
        - 需要从两个堆中来回倒腾数据，对于该种情况，有的虚拟机在堆中分出了几块内存，在内存之间倒腾数据
        - 当程序趋于稳定之后，产生的垃圾少了，甚至没有垃圾了，此时如果还继续使用“停止-复制”法，就显得很浪费了：虚拟机仍然会复制大量的存活数据，所以此时很多虚拟机会转换到另一种状态“标记-清扫”。
    - “标记-清扫”法：一般情况下使用该方法，效率会很低，但是如果确定了垃圾很少了，那就是用它。虚拟机从堆栈或者静态存储区开始遍历对象，遍历到的全部标记，剩余的就是需要被清理的了，标记结束后开始清理，但是此时剩余的是不连续的，就需要重新整理内存了，所以如果垃圾多了，效率就低了。

        这两种方法都是在前台运行的，执行的时候都需要程序先停止
- java虚拟机采用了“自适应”技术（当然现在的已经不这样干了），该技术将“停止-复制”法和“标记-清扫”法结合起来使用，接下来具体讲讲：
    - 把堆内存以较大的块为单位分开，有的大的对象就可以直接独占一个块，在使用“停止-复制”法的时候，大的对象就不需要复制了，只是改变他的代数（每一个块都有一个代数标记，用来记录这个块是否被废弃），而小的对象可以直接复制在标记为废弃的块中，并且整理。当发现效率低了的时候，就切换成“标记-清扫”模式，然后虚拟机会继续跟踪，发现有大量的碎片产生的时候，再切换回来。

## 成员初始化

java中会尽量保证每个变量在使用前都被初始化
- 对于局部变量，如果没有初始化就使用，编译器直接就会给出错误。
- 全局变量，通常会赋予一个默认值(int为0，对象为null等)。



## 构造器初始化

```
cass Cupboard {
    Bowl bowl3 = new Bowl(3);
    static Bowl bowl4 = new Bowl(4);

    Cupboard() {
        print("Cupboard()");
        bowl4.f1(2);
    }

    void f3(int marker) {
        print("f3(" + marker + ")");
    }

    static Bowl bowl5 = new Bowl(5);
}
```
如上代码，类加载的时候，首先执行两个static，然后如果实例化，则会执行```Bowl bowl3 = new Bowl(3);```,然后执行构造方法。


#### 对象创建的过程(有个Dog类)
- 即使没有显示的声明static，构造方法本身也是静态的。当首次创建Dog对象(执行构造方法)，或者首次执行Dog里面的其他静态方法或者变量时(总之就是第一次调用Dog的静态变量或者方法)，类加载器就会查找类路径，定位Dog.class文件
- 载入class文件之后，就会执行类中的所有static初始化操作（变量和静态代码块等不包括静态方法），如果前面是调用的某个方法，那么最后再执行那个方法
- 用new创建对象的时候，就会在堆内存中为Dog分配足够的内存。
- 这块内存清空，这就自动使变量赋了值（int对应为0，对象为null等）

非静态代码块会在创建对象的时候调用，一个类被创建多次，它会执行多次，而静态代码块只会执行一次。






## 数组初始化
```int[] a```这样的操作只是创建了一个数组的引用，并且为这个引用分配内存，但是并没有为这个对象分配内存。所以这里不能指定数组的长度。在初始化的时候才可以制定长度，从而为对象分配内存，初始化可以发生在任意地方，但是用```{1,1,2,3,4}```这种特殊的方式初始化只能在声明的时候进行

当然还可以这样写
```
    int[] a1 = { 1, 2, 3, 4, 5 };
    int[] a2;
    a2= a1;
```
第三行只是给a2复制了a1的引用，所以改变a1或者a2的内容，另一个数组的内容也会跟着变



## enum

 本质上是一个类








