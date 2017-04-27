# Firefly

[![Build Status](https://travis-ci.org/Inari-Soft/inari-firefly.svg?branch=master)](https://travis-ci.org/Inari-Soft/inari-firefly) -- Dependend Projects; commons: [![Build Status](https://travis-ci.org/Inari-Soft/inari-firefly.svg?branch=master)](https://travis-ci.org/Inari-Soft/inari-commons)

**Introduction**

Firefly is a top level 2D game engine framework for Java focusing on intuitive API build on stringent architecture and design.
What makes it different to other java gaming frameworks is its focus on build and manage components and game objects within a component-
entity-system approach and being independent from low level implementation(s).

The main idea of Firefly is to have a top-level 2D game API that comes with a in-build Component-Entity-System architecture that helps
organizing all the game-objects, data and assets in a well defined form and also helps a lot on keeping the game codebase as flexible 
as possible for changes, modify/adding new behavior during the development cycle. What is one of the most impressive benefits of a 
Component-Entity-System based architecture and design approach.
Firefly is implemented on-top of other existing java gaming frameworks like lwjgl or libgdx with the flexibility to change the lower level 
implementation while reusing as much of the game code as possible.


Key features

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
  suggestions possibilities and a fluent interface. 
  
Code example:

```
  context.getComponentBuilder( TextureAsset.TYPE )
      .set( TextureAsset.NAME, "logoTexture" )
      .set( TextureAsset.RESOURCE, "logo.png" )
      .set( TextureAsset.WIDTH, 200 )
      .set( TextureAsset.HEIGHT, 100 )
      .build();
```

- Indexing for Component types and instances for fast access
  Firefly comes with an indexing system that allows to index Java types (Class types) within a defined root type on one hand and on the other
  to index instances (objects) of a specified type. All Components, Entities and Systems are indexed by type and mostly, if needed also by instance
  to guarantee fast access.
  
**NOTE:**

At this time there are three Firefly projects on gitHub. 

- [The firefly-core project](https://github.com/Inari-Soft/inari-firefly) (this project) offers the core API with the entity-system and entity-components and some other useful systems and components for core features like asset-management, view and viewports, audio, animation, text, simple collision management and a few others.   
  The core API also defines the lower-level interfaces but has no implementations for this interfaces. Therefore the firefly-core API is independent from low-level API and can be implemented for different API's like [lwjgl](https://github.com/LWJGL/lwjgl) or [libGDX](https://github.com/libgdx/libgdx)   
  If someone is interested in implementing the firefly API for a particular low level API, this is a good start point.

- [The firefly-lib project](https://github.com/Inari-Soft/inari-firefly-lib) depends on the firefly-core project and offers some additional systems and components that are not part of the firefly-core API. 
  Therefore the firefly-lib project will change mach more then the firefly-core project which remain more stable.

- [The firefly-libGDX project](https://github.com/Inari-Soft/inari-firefly-libGDX) is the only low-level implementation at this time. As the names says, the firefly-core API is implemented on top of the [libGDX](https://github.com/libgdx/libgdx) API.
  If someone wants just get a complete and running firefly API to start coding a game, this is the starting point
  Get and install the firefly API with libGDX implementation is straightforward. Following the instructions here.
  
  **Installation**
  
  - TODO


