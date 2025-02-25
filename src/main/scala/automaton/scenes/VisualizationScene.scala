package automaton.scenes


import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.VBox
import automaton.GUIMain

class VisualizationScene extends Scene(400, 300) :
  root = new VBox {
    children = Seq(
      new Button("Home") {
        onAction = _ => GUIMain.changeScene(new HomeScene)
      }
    )
  }
