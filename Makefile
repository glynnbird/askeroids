A1=		askeroids
CLASSES=	$(A1)/askeroid.class $(A1)/competition.class
VPATH=		$(A1)
all:		$(CLASSES)  

$(A1)/askeroid.class:	askeroid.java alien.java Explosion.java GbShape.java Missile.java Projectile.java Rock.java Velocity.java
			javac -deprecation -target 1.3 -source 1.3 $^

$(A1)/competition.class: competition.java
			javac -deprecation -target 1.3 -source 1.3 $^

$(A1)/askeroids.gif:	images.zip
			unzip -u $^ -d $(A1)

clean::
	@for f in `find $(A1)  -name "*.class"`; do \
	   [ ! -f $$f ] || rm -v $$f ;\
	done
	rm -rf docs

docs::
	javadoc -d docs askeroids

jar::
	jar cf askeroids/askeroids.jar askeroids/*.class askeroids/*.gif

