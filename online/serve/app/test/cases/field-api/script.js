
import { initialize } from "/modules/vzome-legacy.js";

const jsonLine = ( an, label ) => {
  const real = an .evaluate();
  const algebraic = an .toTrailingDivisor();
  const str = an .toString();
  return `  ${JSON.stringify( { real, algebraic } )},  // ${str} == ${label}\n`;
}

initialize() .then( api => {

  let output = "const field = \"sqrtPhi\";\n";
  output += "const values = [\n";
  const field = api .getField( "sqrtPhi" );
  output += jsonLine( field.zero(), '0' );

  const phiInv = field .createPower( -2 );
  const sqrtPhi = field .createPower( 1 );
  const phi = field .createPower( 2 );
  const k = phiInv .times( sqrtPhi );
  const k2 = k .times( k );
  const k3 = k .times( k2 );

  const twoK = k .times( field .createRational( 2, 1 ) );
  output += jsonLine( twoK, '2k' );

  const twoK2 = twoK .times( k );
  output += jsonLine( twoK2, '2k^2' );

  const phiPlusK = phi .plus( k );
  output += jsonLine( phiPlusK, 'phi + k' );

  const phiMinusK = phi .minus( k );
  output += jsonLine( phiMinusK, 'phi - k' );

  const phiPlusKtimesK = phiPlusK .times( k );
  output += jsonLine( phiPlusKtimesK, '(phi + k) * k' );

  const phiPlusKtimesK2 = phiPlusK .times( k2 );
  output += jsonLine( phiPlusKtimesK2, '(phi + k) * k^2' );

  const phiPlusKtimesK3 = phiPlusK .times( k3 );
  output += jsonLine( phiPlusKtimesK3, '(phi + k) * k^3' );

  const phiMinusKtimesK = phiMinusK .times( k );
  output += jsonLine( phiMinusKtimesK, '(phi - k) * k' );

  const phiMinusKtimesK2 = phiMinusK .times( k2 );
  output += jsonLine( phiMinusKtimesK2, '(phi - k) * k^2' );

  const phiMinusKtimesK3 = phiMinusK .times( k3 );
  output += jsonLine( phiMinusKtimesK3, '(phi - k) * k^3' );

  output += "];\n";
  
  const root = document .getElementById( "root" );
  root .innerHTML = output;
});

