import firebase from '@react-native-firebase/app';




// import { MLKitVision } from '@react-native-firebase/ml-vision';
import firebase, { VisionCloudLandmarkRecognizerModelType, MLKitVision } from '@react-native-firebase/ml-vision';

// console.log(MLKitVision.VisionCloudLandmarkRecognizerModelType.LATEST_MODEL);
// console.log(firebase.vision.VisionCloudLandmarkRecognizerModelType.LATEST_MODEL);
console.log(firebase.vision.VisionCloudTextRecognizerModelType.DENSE_MODEL)
console.log(MLKitVision.VisionCloudLandmarkRecognizerModelType.LATEST_MODEL);
console.log(VisionCloudLandmarkRecognizerModelType.LATEST_MODEL);




//
// // checks module exists at root
// console.log(firebase.vision().app.name);
//
// // checks module exists at app level
// console.log(firebase.app().vision().app.name);
//
// // checks statics exist
// console.log(firebase.vision.SDK_VERSION);
//
// // checks statics exist on defaultExport
// console.log(defaultExport.SDK_VERSION);
//
// // checks root exists
// console.log(firebase.SDK_VERSION);
//
// // checks firebase named export exists on module
// console.log(firebaseFromModule.SDK_VERSION);
//
// // checks multi-app support exists
// console.log(firebase.vision(firebase.app()).app.name);
//
// // checks default export supports app arg
// console.log(defaultExport(firebase.app()).app.name);

