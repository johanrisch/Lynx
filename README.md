# Lynx
[![Release](https://jitpack.io/v/johanrisch/Lynx.svg)](https://jitpack.io/#johanrisch/Lynx)


Lynx is a simple, yet powerful and extendable library for storing datatypes on disk using key-value pairs. It consists of one core java library and two optional android libararies of which only one should be included.

The core part can be included by adding jitpack to your list of maven repositories; to do this add the folloing into your root bruild.gradle file:
```gradle
maven { url "https://jitpack.io" }
```
and then adding the dependency to lynx in your modules build.gradle file:
```gradle
compile 'com.github.johanrisch.Lynx:lynx:1.1'
```
Lynx is designed to be as extendable as possible and this is done by dependency inversion on all of it's components. This has one major caveat; namely that the core java libaray does not have all of the implementations needed to actually get started right away. However, there are two android libraries which contains default implementations of the needed components. The difference between the two libraries is that one uses gson and the other one uses jackson for object (de)serialization. So all you have to do is choose one of the lines:
```gradle
compile 'com.github.johanrisch.Lynx:lynx-android-gson:1.1'
````
or
```gradle
compile 'com.github.johanrisch.Lynx:lynx-android-jackson:1.1'
```


##How to use
If you want a full working example on how to use the libaray check out the sample app. At the moment it is very naïvely implemented, a fuller example will be uploaded in the near future.

###Creating an instance
To create an instance of as LynxDiskStorage stroing your CustomClass with a string ke, simply use the builder:
```java
LynxDiskStorage<String, CustomClass> lynxStorage =Lynx.create(new LynxExternalStorageInfo(context), String.class, CustomClass.class)
                                     .withMapper(new LynxObjectMapper())
                                     .withCipherHandler(new LynxCipherHandler(new LynxDefaultPasswordSupplier(context), new LynxBouncyCastleProvider(), new LynxBase64Impl()))
                                     .named("testSecureStorage")
                                     .build();
```

Now you can simply use the lynxStorage to store and retrieve your instances:
```java
lynxStorage.set("aUniqueKey",aCustomClassInstance);
CustomClass loadedInstance = lynxStorage.get("aUniqueKey");
List<CustomClass> allStoredInstances = lynxStorage.getAll();

```
If you do not want to use encryption you can simple use UnSafeLynxCipherHandler which will just pass the bytes through to the FileHandler.

That is pretty much all you need to know to get started using lynx. In the next version I will add more thorogh documentation on how to customize the behaviour of your LynxDiskStorage. But for now you'll have to confide to the javadoc of the interfaces, implementations, or unit tests in order to customize each component.


Happy storing

/Risch
