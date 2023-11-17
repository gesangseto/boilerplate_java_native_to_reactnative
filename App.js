/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React, {useEffect} from 'react';
import {Button, StyleSheet, Text, View} from 'react-native';

import {NativeModules, NativeEventEmitter} from 'react-native';
import {Colors} from 'react-native/Libraries/NewAppScreen';

const {UrovoScanner} = NativeModules;
UrovoScanner.createUrovoScanner((res) => console.log(res));

const event = new NativeEventEmitter(UrovoScanner);
const App = (props) => {
  useEffect(() => {
    event.addListener('BarcodeStremerUrovo', (a) => console.log(a));
  }, []);
  const handlePress = async () => {
    let result = await UrovoScanner.createUrovoScannerPromise();
    console.log(result);
  };
  return (
    <View>
      <Text>Hello</Text>
      <Button title="Test connect" onPress={() => handlePress()}></Button>
    </View>
  );
};

const styles = StyleSheet.create({
  scrollView: {
    backgroundColor: Colors.lighter,
  },
  engine: {
    position: 'absolute',
    right: 0,
  },
  body: {
    backgroundColor: Colors.white,
  },
  sectionContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
    color: Colors.black,
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
    color: Colors.dark,
  },
  highlight: {
    fontWeight: '700',
  },
  footer: {
    color: Colors.dark,
    fontSize: 12,
    fontWeight: '600',
    padding: 4,
    paddingRight: 12,
    textAlign: 'right',
  },
});

export default App;
