# 🤖 Auto GitHub PR — Agent Workflow

> **⚠️ Este workflow NO está referenciado en la estructura de agentes del proyecto (`AGENTS.md`,
> `docs/ARCHITECTURE.md`, etc.) y debe invocarse manualmente.**

---

## 📋 Descripción

Este workflow guía a un agente de IA para implementar una **tarea sencilla** de principio a fin,
siguiendo todas las convenciones del proyecto, y culminando con la **creación automática de una
Pull Request en GitHub**.

El objetivo es que el agente:

1. Implemente el cambio solicitado respetando las convenciones de código y arquitectura.
2. Realice commits con mensajes en formato **Conventional Commits**.
3. Cree una PR completa, asignada y con revisor, lista para ser revisada.

---

## 🔄 Flujo de trabajo

### 1️⃣ Implementar la tarea

- El agente implementa la tarea solicitada por el usuario siguiendo las convenciones del proyecto
  definidas en [`AGENTS.md`](../../AGENTS.md) y [`docs/ARCHITECTURE.md`](../ARCHITECTURE.md).
- Los mensajes de commit **deben** seguir el formato **Conventional Commits**:
  ```
  type(scope): description
  ```
  Ejemplo:
  ```
  feat(tasks): add priority field to task model
  ```
- Cada commit realizado por el agente **debe** incluir el trailer de co-autoría:
  ```
  --trailer "Co-authored-by: <AgentName> <<agent-email>>"
  ```

### 2️⃣ Preguntar por el revisor de la PR

- Antes de crear la PR, el agente **debe preguntar al usuario** a qué usuario de GitHub se le
  asigna como **reviewer** (revisor).
- Si el usuario responde algo como _"al creador"_, _"al owner"_, _"al dueño del repo"_ o similar,
  el agente asignará como revisor a: **`jorge-aranda`**.

### 3️⃣ Asignar la PR al usuario actual

- La PR se **asignará** (_assignee_) al usuario que está interactuando con el agente (el usuario
  actual de la sesión).

### 4️⃣ Título de la PR

- El **título de la PR** será la **descripción del commit** (la parte después de `type(scope):`).
- Ejemplo: si el commit es `feat(tasks): add priority field to task model`, el título de la PR
  será:
  > **add priority field to task model**

### 5️⃣ Rama de origen y destino

| Concepto        | Valor       |
|-----------------|-------------|
| **Rama origen** | `develop`   |
| **Rama destino (base)** | `develop` |

- La rama de feature se crea **desde `develop`**.
- La PR debe mergearse **de vuelta a `develop`**.

### 6️⃣ Descripción de la PR

- El agente **generará un archivo temporal en formato Markdown** (`.md`) con la descripción
  detallada de la PR.
- Este archivo temporal:
  - Debe ser lo **más detallado posible**.
  - Debe usar **estilos visuales atractivos**: encabezados, tablas, listas, emojis, badges, etc.
  - Se utilizará como cuerpo (_body_) de la PR al crearla con `gh pr create --body-file`.
- **🚨 Esto es muy importante:** la descripción debe generarse como archivo temporal `.md` y
  pasarse al comando de creación de la PR.

#### Ejemplo de estructura sugerida para la descripción:

```markdown
## 🎯 Objetivo

Breve descripción del cambio y su motivación.

## 📝 Cambios realizados

- ✅ Cambio 1
- ✅ Cambio 2
- ✅ Cambio 3

## 🧪 Testing

| Tipo de test       | Estado |
|--------------------|--------|
| Tests unitarios    | ✅ Pass |
| Tests integración  | ⏭️ N/A  |

## 📎 Notas adicionales

- Cualquier contexto relevante para el revisor.
```

---

## 🛠️ Ejemplo de ejecución (comandos clave)

```bash
# 1. Crear rama desde develop
git checkout develop
git pull origin develop
git checkout -b feature/my-change

# 2. Implementar cambios y hacer commit
git add .
git commit -m "feat(tasks): add priority field to task model" \
  --trailer "Co-authored-by: Junie <junie@jetbrains.com>"

# 3. Push de la rama
git push origin feature/my-change

# 4. Generar archivo temporal con la descripción de la PR
# (el agente genera /tmp/pr-description.md con contenido detallado)

# 5. Crear la PR
gh pr create \
  --base develop \
  --title "add priority field to task model" \
  --body-file /tmp/pr-description.md \
  --assignee @me \
  --reviewer jorge-aranda
```

---

## ⚙️ Requisitos

- CLI de GitHub (`gh`) autenticada en el entorno del agente.
- Acceso de escritura al repositorio.
- Rama `develop` actualizada.

---

## 📌 Recordatorios

| Regla | Detalle |
|-------|---------|
| 🔀 Rama origen | Siempre desde `develop` |
| 🎯 Rama destino | Siempre hacia `develop` |
| 📝 Título PR | Descripción del commit (sin `type(scope):`) |
| 👤 Assignee | Usuario actual de la sesión |
| 👁️ Reviewer | Preguntar al usuario; por defecto `jorge-aranda` |
| 📄 Descripción | Archivo `.md` temporal, detallado y visual |
| 🏷️ Commits | Conventional Commits + trailer co-author |
| 🚫 No referenciado | Este workflow se invoca **manualmente** |
