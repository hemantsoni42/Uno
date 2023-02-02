---
name: Documentation Update
about: Describe this documentation issue
title: ''
labels: documentation
assignees: ''

---

name: Documentation Update
on: push

jobs:
  update_docs:
    runs-on: ubuntu-latest

    steps:
    - name: Checkout code
      uses: actions/checkout@v2

    - name: Install dependencies
      run: |
        sudo apt-get update
        sudo apt-get install -y <dependency 1> <dependency 2>

    - name: Build documentation
      run: |
        cd docs
        make html

    - name: Deploy documentation
      uses: <deployment action>
      with:
        args: <deployment arguments>
