export const urlViewerCSS = `

svg {
  height: 100%;
}

.vzome-label {
  color: var(--vzome-label-color);
  background-color: var(--vzome-label-background);
  font-size: var(--vzome-label-size);
  font-style: var(--vzome-label-style);
  font-weight: var(--vzome-label-weight);
}

.select__trigger {
  display: inline-flex;
  align-items: center;
  justify-content: space-between;
  width: 200px;
  border-radius: 6px;
  padding: 0 10px 0 16px;
  font-size: 16px;
  line-height: 1;
  height: 40px;
  outline: none;
  background-color: white;
  border: 1px solid hsl(240 6% 90%);
  color: hsl(240 4% 16%);
  transition: border-color 250ms, color 250ms;
}
.select__trigger:hover {
  border-color: hsl(240 5% 65%);
}
.select__trigger:focus-visible {
  outline: 2px solid hsl(200 98% 39%);
  outline-offset: 2px;
}
.select__trigger[data-invalid] {
  border-color: hsl(0 72% 51%);
  color: hsl(0 72% 51%);
}
.select__value {
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
}
.select__value[data-placeholder-shown] {
  color: hsl(240 4% 46%);
}
.select__icon {
  height: 20px;
  width: 20px;
  flex: 0 0 20px;
}
.select__description {
  margin-top: 8px;
  color: hsl(240 5% 26%);
  font-size: 12px;
  user-select: none;
}
.select__error-message {
  margin-top: 8px;
  color: hsl(0 72% 51%);
  font-size: 12px;
  user-select: none;
}
.select__content {
  background-color: white;
  border-radius: 6px;
  border: 1px solid hsl(240 6% 90%);
  box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
  transform-origin: var(--kb-select-content-transform-origin);
  animation: select_contentHide 250ms ease-in forwards;
}
.select__content[data-expanded] {
  animation: select_contentShow 250ms ease-out;
}
.select__listbox {
  overflow-y: auto;
  max-height: 360px;
  margin-block: 0;
  padding: 4px;
}
.select__item {
  font-size: 16px;
  line-height: 1;
  color: hsl(240 4% 16%);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 32px;
  padding: 0 8px;
  position: relative;
  user-select: none;
  outline: none;
}
.select__item[data-disabled] {
  color: hsl(240 5% 65%);
  opacity: 0.5;
  pointer-events: none;
}
.select__item[data-highlighted] {
  outline: none;
  background-color: hsl(200 98% 39%);
  color: white;
}
.select__section {
  padding: 8px 0 0 8px;
  font-size: 14px;
  line-height: 32px;
  color: hsl(240 4% 46%);
}
.select__item-indicator {
  height: 20px;
  width: 20px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
@keyframes select_contentShow {
  from {
    opacity: 0;
    transform: translateY(-8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
@keyframes select_contentHide {
  from {
    opacity: 1;
    transform: translateY(0);
  }
  to {
    opacity: 0;
    transform: translateY(-8px);
  }
}

.settings__overlay,
.progress__overlay {
  position: fixed;
  inset: 0;
  z-index: 50;
  background-color: rgb(0 0 0 / 0.2);
  // animation: overlayHide 250ms ease 100ms forwards;
}
.settings__overlay[data-expanded],
.progress__overlay[data-expanded] {
  // animation: overlayShow 250ms ease;
}
.settings__positioner,
.progress__positioner {
  position: fixed;
  inset: 0;
  z-index: 50;
  display: flex;
  align-items: center;
  justify-content: center;
}
.settings__content,
.progress__content {
  z-index: 50;
  max-width: min(calc(100vw - 16px), 500px);
  border: 1px solid hsl(240 5% 84%);
  border-radius: 6px;
  padding: 16px;
  background-color: white;
  box-shadow: 0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1);
  // animation: progress__contentHide 300ms ease-in forwards;
}
.settings__content[data-expanded],
.progress__content[data-expanded] {
  // animation: contentShow_scale 300ms ease-out;
}


.progress {
  display: flex;
  flex-direction: column;
  gap: 2px;
  width: 300px;
}
.progress__label-container {
  display: flex;
  justify-content: space-between;
}
.progress__label,
.progress__value-label {
  color: hsl(240 4% 16%);
  font-size: 14px;
}
.progress__track {
  height: 10px;
  background-color: hsl(240 6% 90%);
}
.progress__fill {
  background-color: hsl(200 98% 39%);
  height: 100%;
  width: var(--kb-progress-fill-width);
  transition: width 250ms linear;
}
.progress__fill[data-progress="complete"] {
  background-color: #16a34a;
}


.alert-dialog__trigger {
  appearance: none;
  display: inline-flex;
  justify-content: center;
  align-items: center;
  height: 40px;
  width: auto;
  outline: none;
  border-radius: 6px;
  padding: 0 16px;
  background-color: hsl(200 98% 39%);
  color: white;
  font-size: 16px;
  line-height: 0;
  transition: 250ms background-color;
}
.alert-dialog__trigger:hover {
  background-color: hsl(201 96% 32%);
}
.alert-dialog__trigger:focus-visible {
  outline: 2px solid hsl(200 98% 39%);
  outline-offset: 2px;
}
.alert-dialog__trigger:active {
  background-color: hsl(201 90% 27%);
}
.alert-dialog__overlay {
  position: absolute;
  inset: 0;
  z-index: 50;
  background-color: rgb(0 0 0 / 0.2);
  animation: overlayHide 250ms ease 100ms forwards;
}
.alert-dialog__overlay[data-expanded] {
  animation: overlayShow 250ms ease;
}
.alert-dialog__positioner {
  position: absolute;
  inset: 0;
  z-index: 50;
  display: flex;
  align-items: center;
  justify-content: center;
}
.alert-dialog__content {
  z-index: 50;
  max-width: min(calc(100vw - 16px), 500px);
  border: 1px solid hsl(240 5% 84%);
  border-radius: 6px;
  padding: 16px;
  background-color: darkred;
  box-shadow: 0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1);
  animation: contentHide 300ms ease-in forwards;
}
.alert-dialog__content[data-expanded] {
  animation: alert__contentShow 300ms ease-out;
}
.alert-dialog__header {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 12px;
}
.alert-dialog__close-button {
  display: inline-flex;
  height: 16px;
  width: 16px;
  align-items: center;
  border: none;
  padding: unset;
}
.alert-dialog__title {
  font-size: 20px;
  font-weight: 500;
  color: white;
  margin-block: 0px;
}
.alert-dialog__description {
  font-size: 16px;
  color: white;
  margin-block: 0px;
  margin-bottom: 4px;
}


.settings-button {
  appearance: none;
  outline: none;
  background-color: rgba(122, 122, 122, 0.3);
  color: rgba(90,90,90, 1);
  fill: currentColor;
  transition: 250ms background-color;
  position: absolute;
  top: 0.5em;
  right: 0.5em;
  border-style: none;
  width: 5em;
  border-radius: 1.3rem;
  padding-block: 0.4rem;
  padding-inline: 0.7rem;
}
.settings-button:hover {
  background-color: rgba(122, 122, 122, 0.7);
}

.settingsdialog__trigger {
  appearance: none;
  display: inline-flex;
  justify-content: center;
  align-items: center;
  height: 40px;
  width: auto;
  outline: none;
  border-radius: 6px;
  padding: 0 16px;
  background-color: hsl(200 98% 39%);
  color: white;
  font-size: 16px;
  line-height: 0;
  transition: 250ms background-color;
}
.settingsdialog__trigger:hover {
  background-color: hsl(201 96% 32%);
}
.settingsdialog__trigger:focus-visible {
  outline: 2px solid hsl(200 98% 39%);
  outline-offset: 2px;
}
.settingsdialog__trigger:active {
  background-color: hsl(201 90% 27%);
}
.settingsdialog__overlay {
  position: fixed;
  inset: 0;
  z-index: 50;
  background-color: rgb(0 0 0 / 0.2);
  animation: settings__overlayHide 250ms ease 100ms forwards;
}
.settingsdialog__overlay[data-expanded] {
  animation: settings__overlayShow 250ms ease;
}
.settingsdialog__positioner {
  position: absolute;
  inset: 0;
  z-index: 50;
  display: flex;
  align-items: center;
  justify-content: center;
}
.settingsdialog__content {
  z-index: 50;
  max-width: min(calc(100vw - 16px), 500px);
  border: 1px solid hsl(240 5% 84%);
  border-radius: 6px;
  padding: 16px;
  background-color: white;
  box-shadow: 0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1);
  animation: contentHide_scale 300ms ease-in forwards;
}
.settingsdialog__content[data-expanded] {
  animation: contentShow_scale 300ms ease-out;
}

.settingsdialog__body {
  display: flex;
  padding: 1rem;
  gap: 2rem;
}

.settingsdialog__header {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 12px;
}
.settingsdialog__close-button {
  height: 16px;
  width: 16px;
  color: hsl(240 5% 34%);
}
.settingsdialog__title {
  font-size: 20px;
  font-weight: 500;
  color: hsl(240 6% 10%);
}
.settingsdialog__description {
  font-size: 16px;
  color: hsl(240 5% 26%);
}
@keyframes settings__overlayShow {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}
@keyframes settings__overlayHide {
  from {
    opacity: 1;
  }
  to {
    opacity: 0;
  }
}

.corner__icon__button {
  appearance: none;
  display: inline-flex;
  justify-content: center;
  outline: none;
  background-color: rgba(122, 122, 122, 0.3);
  color: rgba(90,90,90, 1);
  fill: currentColor;
  font-size: 12px;
  line-height: 0;
  transition: 250ms background-color;
  position: absolute;
  border-style: none;
  border-radius: 50%;
  height: 4rem;
  width: 4rem;
  align-items: center;
  padding: 0.7rem;
}
.corner__icon__button:hover {
  background-color: rgba(122, 122, 122, 0.7);
}
.corner__icon__button:focus-visible {
  outline: 2px solid hsl(200 98% 39%);
  outline-offset: 2px;
}
.corner__icon__button:active {
  background-color: rgba(0, 0, 0, 0.24);
}


.fullscreen {
  bottom: 0.5em;
  right: 0.5em;
}

.iconbutton__content {
  z-index: 50;
  max-width: min(calc(100vw - 16px), 380px);
  border: 1px solid hsl(240 5% 84%);
  border-radius: 6px;
  padding: 8px;
  background-color: hsl(240 4% 16%);
  color: white;
  font-size: 14px;
  box-shadow: 0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1);
  transform-origin: var(--kb-tooltip-content-transform-origin);
  animation: contentHide_scale 250ms ease-in forwards;
}
.iconbutton__content[data-expanded] {
  animation: contentShow_scale 250ms ease-out;
}


.exports__trigger {
  bottom: 0.5em;
  left: 0.5em;
}
.exports__content {
  min-width: 220px;
  padding: 8px;
  background-color: white;
  border-radius: 6px;
  border: 1px solid hsl(240 6% 90%);
  box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
  outline: none;
  transform-origin: var(--kb-menu-content-transform-origin);
  animation: contentHide_scale 250ms ease-in forwards;
}
.exports__content[data-expanded] {
  animation: contentShow_scale 250ms ease-out;
}
.exports__item {
  font-size: 16px;
  line-height: 1;
  color: hsl(240 4% 16%);
  border-radius: 4px;
  display: flex;
  align-items: center;
  height: 32px;
  padding: 0 8px 0 24px;
  position: relative;
  user-select: none;
  outline: none;
}
.exports__item[data-disabled] {
  color: hsl(240 5% 65%);
  opacity: 0.5;
  pointer-events: none;
}
.exports__item[data-highlighted] {
  outline: none;
  background-color: hsl(200 98% 39%);
  color: white;
}

@keyframes contentShow_scale {
  from {
    opacity: 0;
    transform: scale(0.96);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}
@keyframes contentHide_scale {
  from {
    opacity: 1;
    transform: scale(1);
  }
  to {
    opacity: 0;
    transform: scale(0.96);
  }
}


@keyframes overlayShow {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}
@keyframes overlayHide {
  from {
    opacity: 1;
  }
  to {
    opacity: 0;
  }
}

.switch {
  display: inline-flex;
  align-items: center;
}
.switch__control {
  display: inline-flex;
  align-items: center;
  height: 19px;
  width: 33px;
  border: 1px solid hsl(200 98% 39%);
  border-radius: 12px;
  padding: 0 2px;
  background-color: hsl(200deg 38.91% 75%);
  transition: 250ms background-color;
}
.switch__input:focus-visible + .switch__control {
  outline: 2px solid hsl(200 98% 39%);
  outline-offset: 2px;
}
.switch__control[data-checked] {
  border-color: hsl(200 98% 39%);
  background-color: hsl(200 98% 39%);
}
.switch__thumb {
  height: 17px;
  width: 17px;
  border-radius: 10px;
  background-color: white;
  transition: 250ms transform;
}
.switch__thumb[data-checked] {
  transform: translateX(calc(100% - 1px));
}
.switch__label {
  margin-right: 6px;
  color: hsl(240 6% 10%);
  font-size: 14px;
  user-select: none;
}
`;
