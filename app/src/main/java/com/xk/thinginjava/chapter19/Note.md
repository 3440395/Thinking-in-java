# 第十九章 枚举

## EnumSet
本质是一个long，64位，每一个位表示一个标记，所以理论上可以添加64个元素，但是多了也没报错，超过64个了就用EnumSet的另一个实现JumboEnumSet。它会创建一个long数组（提前声明一堆枚举类型，然后往进去添加）
