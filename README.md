# What Should I Wear Today's Weather? (WWW)

Personalized Clothing Recommendation Mobile Application Based on Current Weather and Temperature

### Introduction

Black turtleneck, gray t-shirt, and black leather jacket. Steve Jobs, Mark Zuckerberg, and Jensen Huang are famous for their iconic outfits. Time is gold, and they do not want to waste it on choosing clothes. They have their own uniform, and they wear it every day. What about you? Do you have a uniform? If not, what should you wear today?

Smart home devices are getting popular. They interact with you in everyday life, such as turning on the lights, playing music, and setting the thermostat, perhaps knowing you better than yourself. Your personal data becomes your personal AI assistant who is together with you 24/7. Samsung released Galaxy S24 featuring with Galaxy AI which has an embedded, on-device AI model. It only stores your data on your device, not sharing it with the cloud.

[What Should I Wear Today's Weather?] (WWW) is a personalized clothing recommendation mobile application based on current weather and temperature. It is designed to help you choose the right clothes for the day. It is simple, easy to use, and efficient. Just open the app, and it will tell you what to wear based on the weather and temperature. No more guessing, no more wasting time. Just wear what you need to wear and enjoy your day.

We adopted Phi-3, a Small Language Model (SLM) able to inference on low computational cost devices, ultimately, aiming to make an AI system that is private, secure, and efficient in personal usage.

### Design

We considered the following AI systems and concluded that they are not suitable for our project;

* Recommendation Systems
  - Lack of Data
  - Complicated in Time Manner (a day hackathon)
* OpenAI APIs
  - Privacy Concerns
  - Costly (LLMs, Money)

**Small Language Model (SLM)** is embedded friendly and has low computational cost, enabling the inference on-device and offline. Phi-3 [2] is an open-source SLM that is capable of generating text and answering questions. The project is based on the Phi-3 model. The recommendation is generated with the query-answer mechanism, using the designed query to fit the recommendation format by the project team.

A prompt is composed by weather, temperature, weight and height of the user, and the clothes of the user. Weather and temperature is fetched from the OpenWeatherMap API [3]. The user's weight, height, and clothes are asked to provide a more accurate recommendation. The prompt is sent to the Phi-3 model, and the recommendation is generated. The recommendation is displayed on the screen.

### Implementation

![Main Activity]()

### References

- [1] Local Chatbot on Android with Phi-3, ONNX Runtime Mobile and ONNX Runtime Generate() API, onnxruntime-inference-examples, GitHub, https://github.com/microsoft/onnxruntime-inference-examples/tree/main/mobile/examples/phi-3/android, accessed in Oct. 13th, 2024.
- [2] Abdin, Marah, et al. "Phi-3 technical report: A highly capable language model locally on your phone." arXiv preprint arXiv:2404.14219 (2024).
- [3] OpenWeatherMap API, https://openweathermap.org/api, accessed in Oct. 13th, 2024.
