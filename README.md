# Le projet MEETING 2020

- [Le projet MEETING 2020](#le-projet-meeting-2020)
  - [Auteur](#auteur)
  - [Présentation](#présentation)
  - [Productions](#productions)
  - [Vidéo](#vidéo)
  - [Licence GPL](#licence-gpl)

Placé à l'extérieur d'une pièce (salle de réunion ou de travail, ...), le système **Meeting** permettra d'accéder en temps réel aux informations de l'espace concerné : il affichera la disponibilité et l'état de confort et permettra de réaliser sa réservation.

## Auteur

Vincent Devine <vincentdevine84@gmail.com>

## Présentation

Placé à l'extérieur d'une pièce (salle de réunion ou de travail, ...), le système **Meeting** permettra d'accéder en temps réel aux informations de l'espace concerné : il affichera la disponibilité et l'état de confort et permettra de réaliser sa réservation.

L'objectif est de proposer une solution simple, alliant flexibilité et ergonomie. Le système affiche de la disponibilité d'accès et le niveau de confort d'une salle de travail ou de réunion.

Le portier connecté est composé :

* d'un micro-contrôleur (ESP32, ...)
* d'un écran tactile
* d'indicateurs lumineux (Leds)
* d'une liaison Bluetooth vers une sonde permettant d'évaluer un “indice de confort”

La sonde est composée :

* d'un capteur de température
* d'un capteur d'hygrométrie
* d'un capteur de qualité d'air

A partir d'une application mobile sous Android et communiquant en UPD via le WiFi, l'utilisateur pourra :

* Lorsqu'une salle a été sélectionnée, visualiser les informations sur la salle (son nom et des informations complémentaires qu'il pourra associer à celle-ci comme sa localisation, sa surface ...), sa disponibilité et les mesures en provenance du module sonde ainsi que son indice de confort.

* configurer les informations d'une salle.

* la possibilité de prendre une salle en indiquant une durée estimée d'occupation de celle-ci. Dans ce cas, le portier lui enverra un code qu'il utilisera pour augmenter la durée d'occupation ou de libérer la salle.

## Productions

- [](dossier-meeting-ir-2020.pdf)
- [](refman-meeting.pdf)
- [](presentation-devine-2020.pptx)

## Vidéo

Lien : https://www.youtube.com/watch?v=CUja0iR_2HU

## Licence GPL

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
