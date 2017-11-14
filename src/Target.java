import java.awt.Color;

import javalib.worldimages.CircleImage;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.WorldImage;

// things on the island the player can interact with
class Target {
  Cell location;
  WorldImage image;
  
  Target(Cell location, Color color){
    this.location = location;
    this.image = new CircleImage(ForbiddenIslandWorld.CELL_SIZE/2, OutlineMode.SOLID, color);
  }
}


// the helicopter 
class Helicopter extends Target {
  
  Helicopter(Cell location, Color color){
    super(location, color);
  }
}
