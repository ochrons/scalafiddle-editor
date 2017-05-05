package scalafiddle.client.component

import diode.{Action, ActionType, NoAction}
import diode.data.{Pot, Ready}
import diode.react.ModelProxy
import japgolly.scalajs.react._
import org.scalajs.dom.raw.HTMLDivElement

import scalajs.js
import scalafiddle.client._
import scalafiddle.shared._

object Sidebar {
  import japgolly.scalajs.react.vdom.all._
  import SemanticUI._

  val accordionRef = Ref[HTMLDivElement]("accordion")

  sealed trait LibMode

  case object ForcedLib extends LibMode

  case object SelectedLib extends LibMode

  case object AvailableLib extends LibMode

  case class Props(data: ModelProxy[FiddleData]) {
    def dispatch[A: ActionType](a: A) = data.dispatchCB(a)
  }

  case class State(showAllVersions: Boolean)

  case class Backend($: BackendScope[Props, State]) {

    def render(props: Props, state: State) = {
      val fd = props.data()
      // filter the list of available libs
      val availableVersions = fd.available.filterNot(lib => fd.libraries.exists(_.name == lib.name))
      // hide alternative versions, if requested
      val available = if (state.showAllVersions)
        availableVersions
      else
        availableVersions.foldLeft(Vector.empty[Library]) {
          case (libs, lib) => if (libs.exists(l => l.name == lib.name)) libs else libs :+ lib
        }
      val libGroups = available.groupBy(_.group).toSeq.sortBy(_._1).map(group => (group._1, group._2.sortBy(_.name)))

      val (authorName, authorImage) = fd.author match {
        case Some(userInfo) if userInfo.id != "anonymous" =>
          (userInfo.name, userInfo.avatarUrl.getOrElse("/assets/images/anon.png"))
        case _ =>
          ("Anonymous", "/assets/images/anon.png")
      }
      div(cls := "sidebar")(
        div(cls := "ui accordion", ref := accordionRef)(
          div(cls := "title large active", "Info", i(cls := "icon dropdown")),
          div(cls := "content active")(
            div(cls := "ui form")(
              div(cls := "field")(
                label("Name"),
                input.text(placeholder := "Untitled", name := "name", value := fd.name,
                  onChange ==> { (e: ReactEventI) => props.dispatch(UpdateInfo(e.target.value, fd.description)) })
              ),
              div(cls := "field")(
                label("Description"),
                input.text(placeholder := "Enter description", name := "description", value := fd.description,
                  onChange ==> { (e: ReactEventI) => props.dispatch(UpdateInfo(fd.name, e.target.value)) })
              ),
              div(cls := "field")(
                label("Author"),
                img(cls := "author", src := authorImage),
                authorName
              )
            )
          ),
          div(cls := "title", "Libraries", i(cls := "icon dropdown")),
          div(cls := "content")(
            div(cls := "ui horizontal divider header", "Selected"),
            div(cls := "liblist")(
              div(cls := "ui middle aligned divided list")(
                fd.forced.map(renderLibrary(_, ForcedLib, props.dispatch)) ++
                  fd.libraries.map(renderLibrary(_, SelectedLib, props.dispatch))
              )
            ),
            div(cls := "ui horizontal divider header", "Available"),
            div(cls := "liblist")(
              libGroups.map { case (group, libraries) =>
                div(
                  h5(cls := "lib-group", group.replaceFirst("\\d+:", "")),
                  div(cls := "ui middle aligned divided list")(
                    libraries.map(renderLibrary(_, AvailableLib, props.dispatch))
                  )
                )
              }
            ),
            div(cls := "ui checkbox")(
              input.checkbox(name := "all-versions", checked := state.showAllVersions,
                onChange --> $.modState(s => s.copy(showAllVersions = !s.showAllVersions))),
              label("Show all versions")
            )
          )
        )
      )
    }

    def renderLibrary(lib: Library, mode: LibMode, dispatch: Action => Callback) = {
      val (action, icon) = mode match {
        case SelectedLib => (DeselectLibrary(lib), i(cls := "remove red icon"))
        case AvailableLib => (SelectLibrary(lib), i(cls := "plus green icon"))
        case ForcedLib => (NoAction, i(cls := "remove grey icon"))
      }
      div(ref := s"${lib.name}${lib.version}", cls := "item")(
        div(cls := "right floated")(
          button(cls := s"mini ui icon basic button ${if (mode == ForcedLib) "disabled" else ""}", onClick --> dispatch(action))(icon)
        ),
        a(href := lib.docUrl, target := "_blank")(
          div(cls := "content left floated", b(lib.name), " ", span(cls := "text grey", lib.version))
        )
      )
    }
  }

  val component = ReactComponentB[Props]("Sidebar")
    .initialState(State(false))
    .renderBackend[Backend]
    .componentDidMount(scope => Callback {
      val accordionNode = scope.refs(accordionRef).get
      JQueryStatic(ReactDOM.findDOMNode(accordionNode)).accordion(js.Dynamic.literal(exclusive = true))
    })
    .build

  def apply(data: ModelProxy[FiddleData]) = component(Props(data))
}
