\NeedsTeXFormat{LaTeX2e}[1995/12/01]
\ProvidesClass{memoriaPFC}[2007/04/23 v0.3.1.1 Plantilla para hacer el PFC de la Facultad de Ingenieria de la Universidad de Deusto. Con modificaciones para hacerla adecuada para los trabajos fin de carrera de la Universidad de Burgos.]
\DeclareOption{borrador}{%
\PassOptionsToPackage{draft}{graphicx}
%\PassOptionsToPackage{hyperref}{draft}
}
\ProcessOptions
% Alvar Arnáiz: Modificado para aumentar la fuente a 11 puntos.
%\LoadClass[10pt,a4paper,twoside]{scrbook}
\LoadClass[11pt,a4paper,twoside]{scrbook}
%\RequirePackage[automark,nouppercase]{scrpage2}
\RequirePackage[automark,markuppercase]{scrpage2}
\RequirePackage[utf8]{inputenc}
\RequirePackage[spanish,activeacute]{babel}
\RequirePackage{titlesec}
\RequirePackage{url}
\RequirePackage{fancybox}
%\RequirePackage{tabularx}
\RequirePackage[pdftex]{graphicx}
\RequirePackage[absolute]{textpos}
\RequirePackage{ifthen}
\RequirePackage[pdftex]{color}
\RequirePackage{fancyvrb}
\RequirePackage{amssymb}
\RequirePackage{multibib}
%\RequirePackage[hypertexnames=false,pdftex=true,colorlinks=true,linkcolor=blue,bookmarks=true]{hyperref}
%\RequirePackage{fancyhdr} % Tiene que ir después del paquete "geometry"
%\RequirePackage{amsfonts}
\RequirePackage{color}
%\RequirePackage{eurosym}
%\RequirePackage[style=long,cols=2,border=none,toc=true]{glossary}
%\RequirePackage{makeidx}
%\RequirePackage{supertabular}
\RequirePackage{listings}

%\setlength{\parindent}{0cm}

% Fuentes
%\newfont{\aufnttitle}{arial at 20pt}
%\newfont{\aufntsubtitle}{arial at 18pt}
%\newfont{\aufntnames}{arial at 12pt}

% Definiendo colores para los listados de código fuente
\definecolor{violet}{rgb}{0.5,0,0.5}
\definecolor{navy}{rgb}{0,0,0.5}

\definecolor{hellgelb}{rgb}{1,1,0.8}
\definecolor{colKeys}{rgb}{0,0,1}
\definecolor{colIdentifier}{rgb}{0,0,0}
\definecolor{colComments}{rgb}{1,0,0}
\definecolor{colString}{rgb}{0,0.5,0}

\lstset{
        float=hbp,
        basicstyle=\ttfamily\small,
        identifierstyle=\color{colIdentifier},
        keywordstyle=\color{colKeys},
        stringstyle=\color{colString},
        commentstyle=\color{colComments},
        columns=flexible,
        tabsize=4,
        frame=single,
        extendedchars=true,
        showspaces=false,
        showstringspaces=false,
        numbers=left,
        numberstyle=\tiny,
        breaklines=true,
        backgroundcolor=\color{hellgelb},
        breakautoindent=true,
        captionpos=b
}

%%
%% INDICE y GLOSARIO
%%

%\makeglossary
%\makeindex

% http://theoval.sys.uea.ac.uk/~nlct/latex/thesis/node25.html
%\glossaryname (por defecto "Glosario")
%\glossary{name=set,description=a collection of objects}

%%
%% PORTADA
%%

% Datos de la portada
%\newcommand{\autores}[4]{\def\@autorUno{#1}\def\@autorDos{#3}\def\@DNIUno{#2}\def\@DNIDos{#4}}
\newcommand{\autores}[2]{\def\@autorUno{#1}\def\@autorDos{#2}}
% Añadido por César García Osorio 6/11/2012
\newcommand{\directores}[2]{
\def\@director{\begin{tabbing}Directores: \= #1\\\> #2\\\end{tabbing}\mbox{ }\vspace*{-4ex}}
}
\newcommand{\titulacion}[1]{\def\@titulacion{#1}}
\newcommand{\director}[1]{\def\@director{Directores: #1}}
\newcommand{\directora}[1]{\def\@director{Directora: #1}}
\newcommand{\resumen}[1]{\def\@resumen{#1}}
\newcommand{\descriptores}[1]{\def\@descriptores{#1}}
% Añadidos por Álvar Arnáiz 29/6/2010
% Modificado por César García Osorio 6/11/2012
%\newcommand{\abstract}[1]{\def\@abstract{#1}}
%\newcommand{\keywords}[1]{\def\@keywords{#1}}
\newsavebox{\@abstract} % <=== declaro un nombre de caja (para el nombre del profesor)
\newsavebox{\@keywords} % <=== declaro un nombre de caja (para el contenido del certificado)
\newenvironment{abstract}{
\begin{lrbox}{\@abstract}
}{
\end{lrbox}
}
\newenvironment{keywords}{
\begin{lrbox}{\@keywords}
}{
\end{lrbox}
}


% Modificado por César García Osorio 6/11/2012
\AtBeginDocument{%
%\ifthenelse{\equal{\@autorDos}{}}{\def\@autores{\@autorUno}}{\def\@autores{\@autorUno{} y \@autorDos}}
\ifthenelse{\equal{\@autorDos}{}}{\def\@autores{\@autorUno}}{\def\@autores{\@autorUno{}\newline\@autorDos{}}}
%\hypersetup{pdftitle={\@title},pdfauthor={\@autores}}
%\hypersetup{pdfsubject={Proyecto Fin de Carrera},pdfkeywords={\@descriptores}}
\pagestyle{CabeceraPFC}
}
\AtEndDocument{%
%% Aparecen todas las fuentes, no solo las que citemos explicitamente en el texto
%\nocite{*}
%\nociteotras{*}
}

% Para dibujar una box de un ancho y alto especifico
\newcommand\xyparbox[4][c]{\mbox{%
  \dimen0=2\fboxrule\advance\dimen0 2\fboxsep
  \dimen1=#2\advance\dimen1 -\dimen0
  \dimen2=#3\advance\dimen2 -\dimen0
  \parbox[#1][\dimen2][c]{\dimen1}{#4}}}

% Alvar Arnáiz: Eliminado para la UBU
% Comando que dibuja la portada
%\newcommand{\hacerportada}{%
%\begin{titlepage}%
% Logotipo de la Titulacion
%\begin{textblock*}{block_width}(x_loc_of_top_left, y_loc_of_top_left)
%\begin{textblock*}{13cm}(1cm,2cm)%%14.8cm
%   \includegraphics[width=13cm]{imgs/esidelogos/\@titulacion.pdf}%
%\end{textblock*}%
% Titulo (Pfc) y subtitulo (tí­tulo del proyecto)
%\begin{textblock*}{14cm}(3.7cm,12.5cm)%
%\begin{textblock*}{14cm}(3.7cm,12cm)%
    %\renewcommand{\baselinestretch}{2}
%    {\noindent\sffamily\Huge\bf Proyecto fin de carrera}\vspace{1.5cm}\\%
%    {\sffamily\huge\bf\@title{}}%
%\end{textblock*}%
% Autores
%\begin{textblock*}{14cm}(3.7cm,19cm)%
%\begin{textblock*}{14cm}(3.7cm,18.5cm)%
%    {\noindent\sffamily\Large\bf\@autorUno{}}\\%
    %\vspace*{1em}\\%
%    \ifthenelse{\equal{\@autorDos}{}}{}{\sffamily\Large\bf\@autorDos{}\\}%
%\end{textblock*}%
% Director y fecha
%\begin{textblock*}{13cm}(3.7cm,27.5cm)%
%\begin{textblock*}{13cm}(3.7cm,26.1cm)%
%  {\noindent\sffamily\Large\bf \@director{}}\\%
%  \vspace*{2ex}\\%
%  {\sffamily\Large\bf Bilbao, \@date{}}%
%\end{textblock*}%
%\end{titlepage}%
%}

% Alvar Arnáiz: Comando para dibujar la portada de la Universidad de Burgos
\newcommand{\ubuportada}{%
\begin{titlepage}%
%\begin{center}%
%    {\noindent\sffamily\Huge\bf Universidad de Burgos}\vspace{2ex}%
%    {\noindent\sffamily\Large\bf Escuela Politécnica Superior}\vspace{2ex}%
%    {\noindent\sffamily\Huge\bf Ingeniería Informática}\vspace{2ex}%
%    {\noindent\sffamily\Large\bf Área de Lenguajes y Sistemas Informáticos}\vspace{8ex}%
%\end{center}%
\begin{center}%
	\includegraphics[width=5cm]{imgs/escudo.pdf}\vspace{8ex}%
\end{center}%
% Titulo (PFC) y subtitulo (título del proyecto)
\begin{center}%
    {\sffamily\huge\bf\@title{}\vspace{8ex}}%
\end{center}%
%% Autores
%\begin{center}%
%    {\noindent\sffamily\Large\bf\@autores}\\%
%\end{center}%
%% Director y fecha
%\begin{textblock*}{13cm}(3.7cm, 25.5cm)%
%  {\noindent\sffamily\Large\bf \@director{}}\\%
%  \vspace*{2ex}\\%
%  {\sffamily\Large\bf Burgos, \@date{}}%
%\end{textblock*}%
\end{titlepage}%
}

% Alvar Arnáiz: Comando para dibujar las portadas de los anexos
\newcommand{\portadasAuxiliares}[1]{%
\begin{titlepage}%
 \begin{center}%
    {\noindent\sffamily\Huge\bf Universidad de Burgos}\vspace{.5cm}%
    
    {\noindent\sffamily\Large\bf Escuela Politécnica Superior}\vspace{.5cm}%
    
    {\noindent\sffamily\Huge\bf Ingeniería Informática}\vspace{.5cm}%
    
    {\noindent\sffamily\Large\bf Área de Lenguajes y Sistemas Informáticos}\vspace{.5cm}%
 \end{center}%
 \begin{center}%
	\includegraphics[width=5cm]{imgs/escudo.pdf}\vspace{.5cm}%
 \end{center}%
 % Titulo de la portada: anexo, apéndice...
 \begin{center}%
    {\sffamily\huge\bf{#1}\vspace{.5cm}}%

    {\sffamily\huge\bf\@title{}\vspace{.5cm}}%
 \end{center}%
  % Autores
 \begin{center}%
    {\noindent\sffamily\Large\bf\@autores}\\%
 \end{center}%
 % Directores
 \begin{textblock*}{13cm}(3.7cm, 25.5cm)%
  {\noindent\sffamily\Large\bf \@director{}}\\%
 \end{textblock*}%
\end{titlepage}
}


%%
%% RESUMEN
%%

% Comando que crea la página resumen


\newcommand{\hacerresumen}{%
\cleardoublepage
\subsection*{Resumen}
\@resumen
\subsection*{Descriptores}
\@descriptores

% Añadido por Álvar Arnáiz 29/6/2010
\cleardoublepage
\subsection*{Abstract}
\@abstract
\subsection*{Keywords}
\@keywords

\thispagestyle{plain}
}



%%
%% FORMATO DE LA PAGINA
%%
% Necesitas que:
% - en la parte superior se deje (hasta el cuerpo) 3cm
% - la cabecera está a 2cm de la parte superior [del cuerpo]
% Por tanto:
%
% 3cm = 1 in + \voffset + \topmargin + \headheight + \headsep
% 2cm = \headsep
%
% Traducimos en órdenes LaTeX.
% Respetamos los valores por defecto de \voffset y \headheight.
% Pon los valores adecuados si quieres cambiarlos:
% \setlength{\voffset}{XXX}
% \setlength{\headheight}{XXX}
% Asignamos valor a \headsep y deducimos el de \topmargin:
\setlength{\headsep}{2cm}
\setlength{\topmargin}{3cm}
\addtolength{\topmargin}{-1in}
\addtolength{\topmargin}{-\voffset}
\addtolength{\topmargin}{-\headheight}
\addtolength{\topmargin}{-\headsep}

% Necesitas que:
% - en la parte inferior se deje (desde el cuerpo) 3cm
% - el pie está a 1cm de la parte inferior
% Por tanto, teniendo en cuenta el espacio dedicado a la cabecera y
% margen superior:
%
% \paperheight = 3 cm + \textheight + 3cm
% 2 cm = \footskip
%
% El valor de \paperheight se asigna mediante la opción a4paper de
% \documentclass o \LoadClass. Falta lo siguiente:
\setlength{\footskip}{2cm}
\setlength{\textheight}{\paperheight}
\addtolength{\textheight}{-6cm}

% Necesitas que:
% - en la parte izquierda del documento se deje 3.5cm
% - en la parte derecha del documento se deje 2.5cm
% Dado que es un documento a dos caras, se tiene:
% 3.5 cm = 1 in + \hoffset + \oddsidemargin
% 2.5 cm = 1 in + \hoffset + \evensidemargin
% \paperwith = 3.5 cm + \textwidth + 2.5 cm
%
% Mantenemos el valor de \hoffset. Modifica la orden siguiente
% si quieres cambiarlo:
%\setlength{\hoffset}{XXX}
\setlength{\oddsidemargin}{3.5 cm}
\addtolength{\oddsidemargin}{-1in}
\addtolength{\oddsidemargin}{-\hoffset}
\setlength{\evensidemargin}{2.5 cm}
\addtolength{\evensidemargin}{-1in}
\addtolength{\evensidemargin}{-\hoffset}
\setlength{\textwidth}{\paperwidth}
\addtolength{\textwidth}{-6cm}

% Para compilar con pdfLaTeX:
\setlength{\pdfpagewidth}{\paperwidth}
\setlength{\pdfpageheight}{\paperheight}

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Esta es la tabla que aparece en la cabecera
\newcommand{\cabeceraimpar}{\textit{\headmark}}
\newcommand{\cabecerapar}{\textit{PROYECTO FIN DE CARRERA}}

% Creamos un estilo de página corporativo - UD
\newpagestyle{CabeceraPFC}
% Definicion de la cabecera:
{(0pt,0pt){\cabeceraimpar\hfill}{\hfill\cabecerapar}{\hfill\headmark\hfill}(0pt,0pt)}%
% Definición del pie de pagina:
{(0pt,0pt){\pagemark\hfill}{\hfill\pagemark}{\hfill\headmark\hfill}(0pt,0pt)}

% Sólo muestra secciones en el encabezado
%\automark[section]{section}
% Sólo muestra capítulos en el encabezado
%\automark[chapter]{section}
\automark[section]{chapter}

% Redefinimos el estilo de página "plain", que es el que aparece
% en las 'primeras páginas' de capitulos, etc. En principio, es
% exactamente igual que la anterior.
\renewpagestyle{plain}%
% Definicion de la cabecera:
{(0pt,0pt){\cabeceraimpar\hfill}{\hfill\cabecerapar}{\hfill\headmark\hfill}(0pt,0pt)}%
% Definición del pie de pagina:
{(0pt,0pt){\pagemark\hfill}{\hfill\pagemark}{\hfill\headmark\hfill}(0pt,0pt)}

% Por defecto, estilo de página corporativo
\pagestyle{CabeceraPFC}

% Esto es para que no aparezca el numero de página en las páginas en blanco.
\makeatletter
\def\cleardoublepage{\clearpage\if@twoside \ifodd\c@page\else
  \hbox{}
  \vspace*{\fill}
  \vspace{\fill}
  \thispagestyle{empty}
  \newpage
  \if@twocolumn\hbox{}\newpage\fi\fi\fi}
\makeatother



%%
%% ENCABEZADOS DE SECCIONES
%%

\renewcommand{\frontmatter}{%
% Eliminamos cabeceras
\pagestyle{empty}%
\renewcommand{\cabecerapar}{}
\pagenumbering{roman}%
}

\renewcommand{\mainmatter}{%
\cleardoublepage
\pagestyle{CabeceraPFC}
%\newcommand{\cabeceraimpar}{\textit{\headmark}}
\renewcommand{\cabecerapar}{\textit{PROYECTO FIN DE CARRERA}}
\pagenumbering{arabic}

% Redefinimos el formato de \chapter
\titleformat{\chapter}[block]{\normalfont\sffamily\huge}{\bfseries\thechapter.}{.5em}{\filleft\bfseries\MakeUppercase}[\titlerule\vspace{2ex}]
\titlespacing*{\chapter}{0pt}{*4}{*1}

% Redefinimos el formato de \section
\titleformat{\section}[block]{\normalfont\sffamily\Large}{\thesection}{.5em}{\bfseries}
\titlespacing*{\section}{0pt}{*4}{*1}

% Redefinimos el formato de \subsection
\titleformat{\subsection}[block]{\normalfont\sffamily\large}{\thesubsection}{.5em}{\bfseries}
\titlespacing*{\subsection}{0pt}{*4}{*1}

% Redefinimos el formato de \subsubsection
\titleformat{\subsubsection}[block]{\normalfont\sffamily\normalsize}{}{0pt}{$\,\blacksquare\;\;\;$\bfseries}
\titlespacing*{\subsubsection}{0pt}{*4}{0.5ex}
}
%fin mainmatter

\renewcommand{\backmatter}{%
% Redefinimos el comando \chapter para los capitulos de la memoria
\titleformat{\chapter}[block]{\normalfont\sffamily\huge}{\bfseries\thechapter.}{.5em}{\filleft\bfseries\MakeUppercase}[\titlerule\vspace{2ex}]
\titlespacing*{\chapter}{0pt}{*4}{*1}
}

%%
%% COMANDOS
%%

% Estas definiciones son para el comando \figura
\newlength{\anchoFigura}
\newlength{\anchoFloat}
\addtolength{\fboxsep}{2\fboxsep}
\renewcommand{\capfont}{\normalfont\normalcolor\sffamily\small}
\renewcommand{\caplabelfont}{\normalfont\normalcolor\sffamily\bfseries\small}

% El comando \figura nos permite insertar figuras comodamente, y utilizando
% siempre el mismo formato. Los parametros son:
% 1 -> Porcentaje del ancho de página que ocupará la figura (de 0 a 1)
% 2 --> Fichero de la imagen
% 3 --> Texto a pie de imagen
% 4 --> Etiqueta (label) para referencias
% 5 --> Opciones que queramos pasarle al \includegraphics
\newcommand{\figura}[5]{%
  \setlength{\anchoFloat}{#1\textwidth}%
  \addtolength{\anchoFloat}{-4\fboxsep}%
  \setlength{\anchoFigura}{\anchoFloat}%
  \begin{figure}
    \begin{center}%
      \Ovalbox{%
        \begin{minipage}{\anchoFloat}%
          \begin{center}%
            \includegraphics[width=\anchoFigura,#5]{#2}%
            \caption{#3}%
            \label{#4}%
          \end{center}%
        \end{minipage}
      }%
    \end{center}%
  \end{figure}%
}

% El comando \figuraSinMarco es igual que \figura, pero no pone marco a la figura.
% Viene bien para diagramas grandes.
\newcommand{\figuraSinMarco}[5]{%
  \begin{figure}%
    \begin{center}%
    \includegraphics[width=#1\textwidth,#5]{#2}%
    \caption{#3}%
    \label{#4}%
    \end{center}%
  \end{figure}%
}

% Referencia a una figura
\newcommand{\ver}[1]{(ver figura~\ref{#1})}


% Para escribir extractos de codigo
%
% Las tabulaciones se substituyen por dos espacios
\fvset{tabsize=2}
% Creamos un nuevo environment de fancyvrb para los ejemplos enmarcados
\DefineVerbatimEnvironment{VerbEj}{BVerbatim}{fontsize=\small,samepage=true,commandchars=\\\{\}}
% Colo de fondo
\definecolor{grisfondo}{gray}{0.9}
% Environment para extractos de codigo
\newenvironment{codigo}%
{\VerbatimEnvironment\begin{Sbox}\begin{VerbEj}}%
{\end{VerbEj}\end{Sbox}\setlength{\fboxsep}{8pt}\begin{center}\fcolorbox{black}{grisfondo}{\TheSbox}\end{center}}

% Otro formato más bonito para código fuente
\newcommand{\codigofuente}[3]{%
  \lstinputlisting[language=#1,caption={#2}]{#3}%
}

% Para el preambulos de los capitulos
\newenvironment{precapitulo}%
{\begin{center}\begin{quote}\sffamily}%
{\end{quote}\end{center}}


%%
%% Miscelanea
%%

% Esto es para que los primeros parrafos no esten indentados
%\makeatletter
%\let\@afterindenttrue\@afterindentfalse
%\@afterindentfalse
%\makeatother

% Definimos el nivel de detalle del Í­ndice: \section
\setcounter{tocdepth}{1}

% Redefinimos el nombre de las tablas.
% En babel está traducido por 'Cuadro' :-?
\addto\captionsspanish{\renewcommand{\tablename}{Tabla}}
\addto\captionsspanish{\renewcommand{\listtablename}{Índice de tablas}}


\newcites{otras}{Bibliografía (Otras fuentes)}

% Comandos para insertar comodamente las dos bibliografias de la memoria:
% la de libros y articulos, y la de 'otras fuentes'.
\newcommand{\bibliografia}[1]{%
\cleardoublepage \addcontentsline{toc}{chapter}{Bibliografí­a (Libros y artículos)}
\renewcommand{\bibname}{BIBLIOGRAFÍA (LIBROS Y ARTÍCULOS)}
%\renewcommand{\bibname}{Bibliografía (Libros y artículos)}
%\renewcommand{\bibname}{BIBLIOGRAFÍA}
%
%\cleardoublepage \addcontentsline{toc}{chapter}{Bibliografí­a}
%\renewcommand{\bibname}{Bibliografía}

%\bibliographystyle{aalphaurl}
\bibliographystyle{plainurl}
\bibliography{#1}
}

\newcommand{\bibliografiaOtras}[1]{%
\cleardoublepage \addcontentsline{toc}{chapter}{Bibliografía (Otras fuentes)}
%\renewcommand{\bibname}{Bibliografí­a (Otras fuentes documentales)}
\renewcommand{\bibname}{BIBLIOGRAFÍA (OTRAS FUENTES)}
\bibliographystyleotras{plainurl}
\bibliographyotras{#1}
}


% Listados de código fuente
\renewcommand{\lstlistlistingname}{Índice de listados}
\renewcommand{\lstlistingname}{Listado}
