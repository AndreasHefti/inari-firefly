# inari-firefly
Firefly is a top-level 2D game engine layer API with focus on organizing entities and components, defining 
systems and communication between systems. With a simple and lightweight low-layer interface that is nevertheless 
easy extandable and allows to implement support for different lower-level API's like libGDX, processing, lwjgl and others.

NOTE: Firefly is in development state and in a very eraly stage.

Introduction

Firefly is a top level 2D game engine framework for Java focusing on intuitive API build on stringent architecture and design.
What makes it different to other java gaming frameworks is its focus on build and manage components and game objects within a component-
entity-system approach and being independent from low level implementation(s).

"There are many good and exciting game frameworks for Java out there but what I always was missing was a good and stringent API that
helps you to manager masses of game objects and their states and life-cycles." 

The main idea of Firefly is to have a top-level 2D game API that comes with a in-build Component-Entity-System architecture that helps
organizing all the game-objects, data and assets in a well defined form and also helps a lot on keeping the game codebase as flexible 
as possible for changes, modify/adding new behavior during the development cycle. What is one of the most impressive benefits of a 
Component-Entity-System based architecture and design approach.
Firefly is implemented on-top of other existing java gaming frameworks like lwjgl or libgdx with the flexibility to change the lower level 
implementation while reusing as much of the game code as possible.


Key features:

- Strong backing on Component and Component-Entity-System approach.
  Almost everything within Firefly is a Component or a Entity (composite of components) or a System

- Lightweight but power-full and easy extendable event system for communication between Systems.  

- Component Attributes
  Every Component in Firefly has a Attribute interface where its attributes (and meta information) can be accessed within attribute maps.
  This makes it possible to serialize the state of a component into what-ever format you need (XML, json...) and also create a Component from.
  Or the attribute mapping allows to access the attributes within a UI tool inspector for example. 

- Independent Lower Level interface definition
  There are a few interface definitions that must be implemented to implement Firefly within a lower level library like lwjgl or libgdx.
  All code that is written against the Firefly API is not affected by the change of the lower level library. 
  Until now only a project with an implementation for libgdx is supported.

- Stringent Component builder API and Context driven
  Firefly is context driven, this means no static method calls like Firefly.files.createAsset(...) and since almost everything within Firefly
  is a Component, there is a component builder that is used to build every kind of Component within the same way and with good code completion 
  suggestions possibilities and a fluent interface. example code:

  context.getComponentBuilder( TextureAsset.TYPE )
      .set( TextureAsset.NAME, "logoTexture" )
      .set( TextureAsset.RESOURCE, "logo.png" )
      .set( TextureAsset.WIDTH, 200 )
      .set( TextureAsset.HEIGHT, 100 )
      .build();

- Indexing for Component types and instances for fast access
  Firefly comes with an indexing system that allows to index Java types (Class types) within a defined root type on one hand and on the other
  to index instances (objects) of a specified type. All Components, Entities and Systems are indexed by type and mostly, if needed also by instance
  to guarantee fast access.

