# ldr2
Progressive logging client

```plantuml

@startuml C4_Elements
!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Container.puml

!define DEVICONS https://raw.githubusercontent.com/tupadr3/plantuml-icon-font-sprites/master/devicons
!include DEVICONS/react.puml
!include DEVICONS/java.puml
!include DEVICONS/atom.puml
!include DEVICONS/html5.puml

Title Level 2 (Containers)

Person(engineer, "Techniker", "Werkstatt Techniker, der Diagnose durchführt.")
System_Boundary(c1, "XC3") {
    Container(electron, "Electron", "", "Wraps the html view in a windows container.", $sprite="atom")
    Container(web_app, "Web UI", "HTML, JS, CSS", "Presents the UI as web page", $sprite="html5")
    Container(react_app, "React App", "React JS", "Renders the web components and accesses the backend.", $sprite="react")
}
    
System_Boundary(c2, "WRT") {
    Container_Ext(wrt, "WRT", "Java / Springboot", "", $sprite="java")
}


System_Ext(vci, "VCI", "Vehicle Communication Interface")
System_Ext(ktm_dealer_net, "KTM Dealer Net", "")

Rel(engineer, electron, "Interacts with", "Browser")
Rel_R(electron, web_app, "", "")
Rel_R(web_app, react_app, "Interacts with", "Browser")
Rel(react_app, wrt, "Interacts with", "Http / REST")
Rel(wrt, vci, "Communicates with", "")
Rel_R(react_app, ktm_dealer_net, "Communicates with", "")
@enduml

```
