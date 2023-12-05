/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React, {useEffect, useState} from 'react';
import {Text, View} from 'react-native';
import {NativeModules, NativeEventEmitter} from 'react-native';
const {UrovoScanner} = NativeModules;
const event = new NativeEventEmitter(UrovoScanner);
const App = (props) => {
  const [result, setResult] = useState('');
  useEffect(() => {
    event.addListener('urovo_broadcast_intent', (a) => setResult(a));
  }, []);
  return (
    <View>
      <Text>Hello, ready for scan</Text>
      <Text>{result}</Text>
    </View>
  );
};

export default App;
