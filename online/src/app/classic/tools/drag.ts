
import { MOUSE, Quaternion, Vector2, Vector3 } from 'three';

class DragHandler
{
  public enabled = true

  public screen = { left: 0, top: 0, width: 0, height: 0 }

  public rotateSpeed = 1.0

  public keys: [string, string, string] = ['KeyA' /*A*/, 'KeyS' /*S*/, 'KeyD' /*D*/]

  public mouseButtons = {
    LEFT: MOUSE.ROTATE,
  }

  private _domElement: HTMLElement | undefined;
  public get domElement(): HTMLElement | undefined {
    return this._domElement;
  }
  public set domElement(value: HTMLElement | undefined) {
    this._domElement = value;
  }

  // internals
  private STATE = {
    NONE: -1,
    ROTATE: 0,
    TOUCH_ROTATE: 1,
  }

  private EPS = 0.000001

  private _state = this.STATE.NONE
  private _keyState = this.STATE.NONE
  private _movePrev = new Vector2()
  private _moveCurr = new Vector2()

  private setDrag: any

  constructor( setDrag: any, domElement: HTMLElement, startEvent: any )
  {
    this.setDrag = setDrag;

    // connect events
    if (domElement !== undefined) this.connect(domElement)

    startEvent && this.onMouseDown( startEvent );

    // force an update at start
    this.doDrag()
  }

  private onCircleVector = new Vector2()

  private getMouseOnCircle = (pageX: number, pageY: number): Vector2 => {
    this.onCircleVector.set( pageX, -pageY );    
    return this.onCircleVector
  }

  private moveDirection = new Vector3()
  private angle: number = 0

  public doDrag = (): void => {
    this.moveDirection .set(this._moveCurr.x - this._movePrev.x, this._moveCurr.y - this._movePrev.y, 0)
    this.angle = this.moveDirection.length() * Math.PI / 180 // convert degrees (as pixels) to radians

    if (this.angle) {      
      this.setDrag( { direction: this.moveDirection.normalize(), angle: this.angle } )
    }

    this._movePrev.copy(this._moveCurr)
  }

  public handleResize = (): void => {
    if (!this.domElement) return
    const box = this.domElement.getBoundingClientRect()
    // adjustments come from similar code in the jquery offset() function
    const d = this.domElement.ownerDocument.documentElement
    this.screen.left = box.left + window.scrollX - d.clientLeft
    this.screen.top = box.top + window.scrollY - d.clientTop
    this.screen.width = box.width
    this.screen.height = box.height
  }

  public reset = (): void => {
    this._state = this.STATE.NONE
    this._keyState = this.STATE.NONE
  }

  private keydown = (event: KeyboardEvent): void => {
    if (this.enabled === false) return

    window.removeEventListener('keydown', this.keydown)

    if (this._keyState !== this.STATE.NONE) {
      return
    } else if (event.code === this.keys[this.STATE.ROTATE] ) {
      this._keyState = this.STATE.ROTATE
    }
  }

  private onPointerDown = (event: PointerEvent): void => {
    if (this.enabled === false) return

    switch (event.pointerType) {
      case 'mouse':
      case 'pen':
        this.onMouseDown(event)
        break

      // TODO touch
    }
  }

  private onPointerMove = (event: PointerEvent): void => {
    if (this.enabled === false) return

    switch (event.pointerType) {
      case 'mouse':
      case 'pen':
        this.onMouseMove(event)
        break

      // TODO touch
    }
  }

  private onPointerUp = (event: PointerEvent): void => {
    if (this.enabled === false) return

    switch (event.pointerType) {
      case 'mouse':
      case 'pen':
        this.onMouseUp()
        break

      // TODO touch
    }
  }

  private keyup = (): void => {
    if (this.enabled === false) return

    this._keyState = this.STATE.NONE

    window.addEventListener('keydown', this.keydown)
  }

  private onMouseDown = (event: MouseEvent): void => {
    if (!this.domElement) return
    if (this._state === this.STATE.NONE) {
      switch (event.button) {
        case this.mouseButtons.LEFT:
          this._state = this.STATE.ROTATE
          break

        default:
          this._state = this.STATE.NONE
      }
    }

    const state = this._keyState !== this.STATE.NONE ? this._keyState : this._state

    if (state === this.STATE.ROTATE) {
      this._moveCurr.copy(this.getMouseOnCircle(event.pageX, event.pageY))
      this._movePrev.copy(this._moveCurr)
    }

    this.domElement.ownerDocument.addEventListener('pointermove', this.onPointerMove)
    this.domElement.ownerDocument.addEventListener('pointerup', this.onPointerUp)

    // this.dispatchEvent(this.startEvent)
  }

  private onMouseMove = (event: MouseEvent): void => {
    if (this.enabled === false) return

    const state = this._keyState !== this.STATE.NONE ? this._keyState : this._state

    if (state === this.STATE.ROTATE ) {
      this._movePrev.copy(this._moveCurr)
      this._moveCurr.copy(this.getMouseOnCircle(event.pageX, event.pageY))
    }
  }

  private onMouseUp = (): void => {
    if (!this.domElement) return
    if (this.enabled === false) return

    this._state = this.STATE.NONE

    this.domElement.ownerDocument.removeEventListener('pointermove', this.onPointerMove)
    this.domElement.ownerDocument.removeEventListener('pointerup', this.onPointerUp)

    // this.dispatchEvent(this.endEvent)
  }

  private mousewheel = (event: WheelEvent): void => {
    if (this.enabled === false) return

  }

  private touchstart = (event: TouchEvent): void => {
    if (this.enabled === false) return

    event.preventDefault()

    this._state = this.STATE.TOUCH_ROTATE
    this._moveCurr.copy(this.getMouseOnCircle(event.touches[0].pageX, event.touches[0].pageY))
    this._movePrev.copy(this._moveCurr)

    // this.dispatchEvent(this.startEvent)
  }

  private touchmove = (event: TouchEvent): void => {
    if (this.enabled === false) return

    event.preventDefault()

    this._movePrev.copy(this._moveCurr)
    this._moveCurr.copy(this.getMouseOnCircle(event.touches[0].pageX, event.touches[0].pageY))
}

  private touchend = (event: TouchEvent): void => {
    if (this.enabled === false) return

    switch (event.touches.length) {
      case 0:
        this._state = this.STATE.NONE
        break

      case 1:
        this._state = this.STATE.TOUCH_ROTATE
        this._moveCurr.copy(this.getMouseOnCircle(event.touches[0].pageX, event.touches[0].pageY))
        this._movePrev.copy(this._moveCurr)
        break
    }

    // this.dispatchEvent(this.endEvent)
  }

  private contextmenu = (event: MouseEvent): void => {
    if (this.enabled === false) return

    event.preventDefault()
  }

  // https://github.com/mrdoob/three.js/issues/20575
  public connect = (domElement: HTMLElement): void => {
    if ((domElement as any) === document) {
      console.error(
        'TrackballControls: "document" should not be used as the target "domElement". Please use "renderer.domElement" instead.',
      )
    }
    this.domElement = domElement
    this.domElement.addEventListener('contextmenu', this.contextmenu)

    this.domElement.addEventListener('pointerdown', this.onPointerDown)
    this.domElement.addEventListener('wheel', this.mousewheel)

    this.domElement.addEventListener('touchstart', this.touchstart)
    this.domElement.addEventListener('touchend', this.touchend)
    this.domElement.addEventListener('touchmove', this.touchmove)

    this.domElement.ownerDocument.addEventListener('pointermove', this.onPointerMove)
    this.domElement.ownerDocument.addEventListener('pointerup', this.onPointerUp)

    window.addEventListener('keydown', this.keydown)
    window.addEventListener('keyup', this.keyup)

    this.handleResize()
  }

  public dispose = (): void => {
    if (!this.domElement) return
    this.domElement.removeEventListener('contextmenu', this.contextmenu)

    this.domElement.removeEventListener('pointerdown', this.onPointerDown)
    this.domElement.removeEventListener('wheel', this.mousewheel)

    this.domElement.removeEventListener('touchstart', this.touchstart)
    this.domElement.removeEventListener('touchend', this.touchend)
    this.domElement.removeEventListener('touchmove', this.touchmove)

    this.domElement.ownerDocument.removeEventListener('pointermove', this.onPointerMove)
    this.domElement.ownerDocument.removeEventListener('pointerup', this.onPointerUp)

    window.removeEventListener('keydown', this.keydown)
    window.removeEventListener('keyup', this.keyup)
  }
}

export { DragHandler }