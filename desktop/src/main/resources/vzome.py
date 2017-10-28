
import com.vzome.api
from java.io import FileInputStream                      


class Number( object ):
    '''
    classdocs
    '''

    @classmethod
    def _fromNumber( cls, number ):
        self = Number.__new__(Number)
        self .repr = number
        return self

    def _asNumber( self ):
        return self.repr

    def __add__( self, rhs ):
        if isinstance( rhs, self.__class__ ):
            return Number ._fromNumber( self.repr .plus( rhs.repr ) )
        elif isinstance( rhs, int ):
            return Number ._fromNumber( self.repr .plus( com.vzome.api.Number( self.repr.getField(), [rhs] ) ) )
        else:
            raise TypeError( "unsupported operand type(s) for +: '{}' and '{}'" ).format( self.__class__, type( rhs ) )

    def __sub__( self, rhs ):
        if isinstance( rhs, self.__class__ ):
            return Number ._fromNumber( self.repr .plus( rhs.repr ) )
        elif isinstance( rhs, int ):
            return Number ._fromNumber( self.repr .minus( com.vzome.api.Number( self.repr.getField(), [rhs] ) ) )
        else:
            raise TypeError( "unsupported operand type(s) for +: '{}' and '{}'" ).format( self.__class__, type( rhs ) )

    def __neg__( self ) :
        return Number ._fromNumber( self.repr .negate() )

    def __mul__( self, rhs ) :
        if isinstance( rhs, self.__class__ ):
            return Number ._fromNumber( self.repr .times( rhs.repr ) )
        elif isinstance( rhs, int ):
            return Number ._fromNumber( self.repr .times( com.vzome.api.Number( self.repr.getField(), [rhs] ) ) )
        else:
            raise TypeError( "unsupported operand type(s) for +: '{}' and '{}'" ).format( self.__class__, type( rhs ) )

    def __float__( self ) :
        return self.repr .value()

    def __str__( self ) :
        return self.repr .toString()

    def __hash__( self ) :
        return self.repr .hashCode()

    def __eq__( self, other ) :
        return other != None and self.repr .equals( other._asNumber() )


class Vector( object ):

    @classmethod
    def _fromVector( cls, vec ):
        self = Vector.__new__(Vector)
        self .repr = vec
        return self

    def __init__( self, x, y, z ):
        self.repr = com.vzome.api.Vector( x, y, z )

    def _asVector( self ):
        return self.repr

    def __add__( self, rhs ):
        if isinstance( rhs, self.__class__ ):
            return Vector ._fromVector( self .repr .plus( rhs.repr ) )
        else:
            raise TypeError( "unsupported operand type(s) for +: '{}' and '{}'" ).format( self.__class__, type( rhs ) )

    def __sub__( self, rhs ):
        if isinstance( rhs, self.__class__ ):
            return Vector ._fromVector( self .repr .minus( rhs.repr ) )
        else:
            raise TypeError( "unsupported operand type(s) for +: '{}' and '{}'" ).format( self.__class__, type( rhs ) )

    def __neg__( self ) :
        return Vector( self .repr .negate() )

    def __mul__( self, rhs ) :
        if isinstance( rhs, Number ):
            return Vector ._fromVector( self .repr .times( rhs.repr ) )
        elif isinstance( rhs, int ):
            return Vector ._fromVector( self .repr .times( com.vzome.api.Number( self .repr .getField(), [rhs] ) ) )
        else:
            raise TypeError( "unsupported operand type(s) for +: '{}' and '{}'" ).format( self.__class__, type( rhs ) )

    def __str__( self ) :
        return self.repr .toString()

    def __hash__( self ) :
        return self.repr .hashCode()

    def __eq__( self, other ) :
        return other != None and self.repr .equals( other._asVector() )
    
    def isParallel( self, other ) :
        return self .repr .isParallel( other ._asVector() )


class Ball( object ):

    def __init__( self, ball ):
        self.repr = ball

    def _getRepr( self ):
        return self.repr

    def location( self ):
        return Vector ._fromVector( self .repr .location() )

    def __hash__( self ) :
        return self.repr .hashCode()

    def __eq__( self, other ) :
        return other != None and self.repr .equals( other._getRepr() )
    
    def __str__( self ) :
        return self.repr .toString()


class Strut( object ):

    def __init__( self, strut ):
        self.repr = strut

    def _getRepr( self ):
        return self .repr

    def location( self ):
        return Vector ._fromVector( self .repr .location() )

    def offset( self ):
        return Vector ._fromVector( self .repr .offset() )

    def __hash__( self ) :
        return self.repr .hashCode()

    def __eq__( self, other ) :
        return other != None and self.repr .equals( other._getRepr() )
    
    def __str__( self ) :
        return self.repr .toString()


class Panel( object ):

    def __init__( self, panel ):
        self.repr = panel

    def _getRepr( self ):
        return self .repr

    def __hash__( self ) :
        return self.repr .hashCode()

    def __eq__( self, other ) :
        return other != None and self.repr .equals( other._getRepr() )
    
    def __str__( self ) :
        return self.repr .toString()


class Command( object ):

    def __init__( self, cmd ):
        self.repr = cmd
        
    def log( self, text ):
        self .repr .log( text )

    def _asCommand( self ):
        return self.repr
    
    def newNumber( self, *args ):
        return Number ._fromNumber( self .repr .newNumber( args ) )

    def selection( self ):
        result = []
        for obj in self.repr.selection():
            if isinstance( obj, com.vzome.api.Ball ):
                result .append( Ball( obj ) )
            elif isinstance( obj, com.vzome.api.Strut ):
                result .append( Strut( obj ) )
            elif isinstance( obj, com.vzome.api.Panel ):
                result .append( Panel( obj ) )
        return result

    def addBall( self, loc ):
        return Ball( self .repr .addBall( loc ._asVector() ) )

    def addStrut( self, start, end ):
        return Strut( self .repr .addStrut( start ._asVector(), end ._asVector() ) )

    def addPanel( self, *vertices ):
        def fun( vertex ):
            return vertex ._asVector();
        return Panel( self .repr .addPanel( map( fun, vertices ) ) )

    def select( self, obj ):
        self .repr .select( obj ._getRepr() )


class Document( object ):

    def __init__( self, doc ):
        self.repr = doc

    def _getRepr( self ):
        return self .repr
        
    def newCommand( self ):
        return Command( self .repr .newCommand() )


def open( filename ):
    fis = FileInputStream( filename )
    return Document( application .loadDocument( fis ) )


application = com.vzome.api.Application()                          
