# inari-firefly
Firefly is a top-level 2D game engine layer API with focus on organizing entities and components, defining 
systems and communication between systems. With a simple and lightweight low-layer interface that is nevertheless 
easy extandable and alows to implement support for different lower-level API's like libGDX, processing, lwjgl and others.

NOTE: Firefly is in development and in a very eraly stage.

The core of Firefly engine is build on a Entity-Component-System that separates data form logic. The Firefly engine is 
heighly component based and adds a component-attribute-support that is also a interface for over- or underling API's 
such as a autoring tool or a import/export serializer.

The Components and Entites are indexed within a sophisticated but minimalistic indexing mechanism to 
grantee fast access and good performance.
