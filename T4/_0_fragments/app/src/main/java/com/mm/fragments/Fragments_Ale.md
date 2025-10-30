El objetivo de esta práctica es trabajar con **Fragments** e integrar las primeras animaciones entre pantallas.

Un [Fragment](https://developer.android.com/reference/android/app/Fragment.html) representa un comportamiento o una parte de la interfaz de usuario en una [Activity](https://developer.android.com/reference/android/app/Activity.html).

Puedes combinar múltiples fragmentos en una sola actividad para crear una IU multipanel y volver a usar un fragmento en múltiples actividades. Puedes pensar en un fragmento como una sección modular de una actividad que tiene su ciclo de vida propio, recibe sus propios eventos de entrada y que puedes agregar o quitar mientras la actividad se esté ejecutando (algo así como una "subactividad" que puedes volver a usar en diferentes actividades).

Programaremos un proceso de onboarding para una app:

![anim](https://educacionadistancia.juntadeandalucia.es/centros/sevilla/draftfile.php/15051/user/draft/118379148/aad2d5607d124407.gif)

## Vista general del componente Navigation

El componente Navigation consiste en tres partes clave:

1. **Navigation Graph** (XML resource) - Es un recurso que contiene toda la información relativa a la navegación en un lugar centralizado. Esto incluye todos los lugares de la app, conocidas como **destinos**, y todos los caminos posibles que un usuario puede tomar en la app

   ![](https://educacionadistancia.juntadeandalucia.es/centros/sevilla/draftfile.php/15051/user/draft/118379148/nav_graph.png)

2. **NavHostFragment** (Layout XML) - Es un widget especial que añades al layout. En él se muestran los diferentes destinos del Grafo de Navegación.

   ![](https://educacionadistancia.juntadeandalucia.es/centros/sevilla/draftfile.php/15051/user/draft/118379148/navhost.png)

3. **NavController** (Java object) - Es un objeto que realiza un seguimiento de la posición actual dentro del grafo de navegación. Realiza el intercambio del contenido de destino en el NavHostFragment según el usuario se mueve por el grafo de navegación.

Cuando navegas, utilizas el objeto NavController, diciéndole dónde quieres ir o qué camino quieres tomar en **Grafo de Navegación**. El NavController mostrará el destino apropiado en el NavHostFragment.

## Crea el proyecto

1. Selecciona **Empty Views Activity** como plantilla para la MainActivity

Añade las siguientes dependencias para incluir el soporte Navigation en **build.gradle.kts** (sección Module):
```kotlin
dependencies {
    // ...
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:2.5.3")
    implementation("androidx.navigation:navigation-ui:2.5.3")
}
```

- Puedes añadir las dependencias ahora mismo con la opción **Sync Now**
- O también puedes dejar que sea el propio Android Studio el que las añada automáticamente cuando crees el grafo de navegación en la siguiente sección.

## Crea el Grafo de Navegación

Sobre la carpeta raiz del proyecto haz clic-derecho y selecciona **New Android Resource File**

Introduce el nombre del fichero del grafo, por ejemplo: nav_graph.  
En el Resource type selecciona Navigation

![ANIM](https://educacionadistancia.juntadeandalucia.es/centros/sevilla/draftfile.php/15051/user/draft/118379148/e156c72cca2fe4c0.gif)

### Añade los fragments al grafo de navegación

Añadiremos 3 destinos al grafo de navegación. Dos destinos para el *Onboarding*, y uno que será la pantalla *Home* de la app.

![](https://educacionadistancia.juntadeandalucia.es/centros/sevilla/draftfile.php/15051/user/draft/118379148/destinos.png)

Abre el fichero **res/navigation/nav_graph.xml** en modo Design, y haz click sobre el icono "**New Destination**"

![](https://educacionadistancia.juntadeandalucia.es/centros/sevilla/draftfile.php/15051/user/draft/118379148/1ae033cc41249850.png)

Haz click en **Create new destination**

![](https://educacionadistancia.juntadeandalucia.es/centros/sevilla/draftfile.php/15051/user/draft/118379148/3ecc4861e723bbec.png)

Seleciona **Fragment (Blank)** como plantilla para el Fragment:

![](https://educacionadistancia.juntadeandalucia.es/centros/sevilla/draftfile.php/15051/user/draft/118379148/11840df1fe14c150.png)

Introduce el nombre **Onboarding1Fragment**:

![](https://educacionadistancia.juntadeandalucia.es/centros/sevilla/draftfile.php/15051/user/draft/118379148/fa125b95b4ebea7e.png)

Repite el proceso para crear los dos siguientes fragments:

- **Onboarding2Fragment**
- **HomeFragment**

![](https://educacionadistancia.juntadeandalucia.es/centros/sevilla/draftfile.php/15051/user/draft/118379148/creardestinos.png)

El **icono ![](https://gerardfp.github.io/dam/mobils/fragments/img/homefragment.png)** sobre el onboarding1Fragment indica que este es el destino que se mostrará en primer lugar: el **Start Destination**.

Puedes cambiar en cualquier momento el Start Destination seleccionando un destino y clicando en el icono ![](https://gerardfp.github.io/dam/mobils/fragments/img/homefragment.png) de la barra.

### Conecta los destinos

La navegación entre destinos se define creando **acciones** de navegación.

Para crear una **acción** hay que hacer clic sobre el manejador de origen de un destino y arrastrarlo sobre el siguiente destino:

![ANIM](https://educacionadistancia.juntadeandalucia.es/centros/sevilla/draftfile.php/15051/user/draft/118379148/7fdd44581bfb952a.gif)

Crea las 2 acciones de navegación:

1. Del **onboarding1Fragment** al **onboarding2Fragment**
2. Del **onboarding2Fragment** al **homeFragment**

![](https://educacionadistancia.juntadeandalucia.es/centros/sevilla/draftfile.php/15051/user/draft/118379148/bb64e7786187d362.png)

Observa que en el código XML del archivo res/navigation/nav_graph.xml se han añadido las `<action>` de navegación:

**res/navigation/nav_graph.xml**
```xml
...

<action
    android:id="@+id/action_onboarding1Fragment_to_onboarding2Fragment"
    app:destination="@id/onboarding2Fragment"/>
...

<action
    android:id="@+id/action_onboarding2Fragment_to_homeFragment"
    app:destination="@id/homeFragment" />
...
```

Más adelante utilizaremos los identificadores de estas acciones para implementar la navegación.

## Añade el NavHost

En el layout de la MainActivity (**activity_main.xml**) añade un `<FragmentContainerView>` que será el NavHostFragment:
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <androidx.fragment.app.FragmentContainerView
        android:name="androidx.navigation.fragment.NavHostFragment"
        android:id="@+id/nav_host_fragment"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:defaultNavHost="true"
        app:navGraph="@navigation/nav_graph" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

Puedes ver el NavHostFragment como un hueco en el cual se irán poniendo los diferentes destinos según se vaya navegando. Observa que lo hemos enlazado con el grafo de navegación que acabamos de crear mediante el atributo **app:navGraph="@navigation/nav_graph"**

## Diseña de las pantallas

### Añade imágenes vectoriales a cada fragment

Puedes descargar las imágenes desde los siguientes enlaces a bancos de assets:

- [unDraw](https://undraw.co/)
- [DrawKit](https://www.drawkit.io/)
- [humaaans](https://www.humaaans.com/)
- [design.ai](https://designs.ai/graphicmaker/)

Usaremos las siguientes imágenes que puedes descargar de ejemplo:

- [onboarding1.svg](https://educacionadistancia.juntadeandalucia.es/centros/sevilla/draftfile.php/15051/user/draft/118379148/onboarding1.svg)
- [onboarding2.svg](https://educacionadistancia.juntadeandalucia.es/centros/sevilla/draftfile.php/15051/user/draft/118379148/onboarding2.svg)

Encima del directorio app, haz clic-derecho y selecciona New Vector Asset

![](https://educacionadistancia.juntadeandalucia.es/centros/sevilla/draftfile.php/15051/user/draft/118379148/6069e0cb451fd375.png)

Selecciona Asset Type: Local file e introduce la ruta del fichero onboarding1.svg:

![](https://educacionadistancia.juntadeandalucia.es/centros/sevilla/draftfile.php/15051/user/draft/118379148/e6e53296705f0522.png)

Repite lo mismo para la imagen onboarding2.svg.

![](https://educacionadistancia.juntadeandalucia.es/centros/sevilla/draftfile.php/15051/user/draft/118379148/af90d88ca5157c.png)

### Diseña el layout

Por defecto, el layout de los fragments creados contiene un FrameLayout. Este layout está pensado para contener un único *child*. Cambiémoslo por un ConstraintLayout:

Abre el fichero **res/layout/fragment_onboarding1.xml** en modo Code y sustituye el FrameLayout por un ConstraintLayout:

![anim](https://educacionadistancia.juntadeandalucia.es/centros/sevilla/draftfile.php/15051/user/draft/118379148/26696c090d224e8f.gif)

En cada pantalla añadiremos una imagen, un texto y un botón:

Para añadir una imagen usamos el widget ImageView. La imagen que se debe mostrar se define en el atributo android:src.

Es importante establecer el atributo **android:adjustViewBounds** a **true**. Esto hace que los límites (el borde) del ImageView se ajusten al tamaño de la imagen.
```xml
<ImageView
android:layout_width="wrap_content"
android:layout_height="wrap_content"
android:adjustViewBounds="true"
android:src="@drawable/ic_onboarding1"/>
```

También puedes usar el modo Design para añadir la imagen:

![anim](https://educacionadistancia.juntadeandalucia.es/centros/sevilla/draftfile.php/15051/user/draft/118379148/15391e12dc62f033.gif)

![](https://educacionadistancia.juntadeandalucia.es/centros/sevilla/draftfile.php/15051/user/draft/118379148/6b568aa0e98e2a71.png)

Añadimos también el TextView y el Button.

Modificamos algunos atributos para añadir colores de fondo, paddings, tamaños de letra, etc... También creamos las *constraints* necesarias y definimos unos **identificadores** apropiados.

El layout **res/layout/fragment_onboarding1.xml** queda finalmente así:
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
xmlns:app="http://schemas.android.com/apk/res-auto"
android:layout_width="match_parent"
android:layout_height="match_parent"
android:background="#4CAF50"
android:padding="32dp">

<ImageView
    android:id="@+id/imagen"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:adjustViewBounds="true"
    android:src="@drawable/ic_onboarding1"
    app:layout_constraintBottom_toTopOf="@+id/texto"
    app:layout_constraintTop_toTopOf="parent"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toRightOf="parent"/>

<TextView
    android:id="@+id/texto"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="!Discover the garlic soup!"
    android:textColor="#FFFFFF"
    android:textSize="30sp"
    app:layout_constraintBottom_toTopOf="@+id/botonSiguiente"
    app:layout_constraintTop_toBottomOf="@id/imagen"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toRightOf="parent"/>

<Button
    android:id="@+id/botonSiguiente"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:backgroundTint="#FFEB3B"
```